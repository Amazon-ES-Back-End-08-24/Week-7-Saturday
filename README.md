# Week 7
## Relaciones en JPA

### One-to-One
- Esta relación se utiliza cuando una entidad está asociada con una sola instancia de otra entidad.
- **Ejemplo en la app de Pizzería:** Una **Pizzería** tiene un **localManager** (un **Empleado**), y un **Empleado** gestiona una sola **Pizzería**.

```java
@Entity 
// Lombok annotation for getters, setters, etc.
public class Pizzeria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne //(cascade = CascadeType.ALL)
    @JoinColumn(name = "local_manager_id", referencedColumnName = "id")
    private Employee localManager;
}

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // sin esto sería unidireccional, con ello bidireccional
    @OneToOne(mappedBy = "localManager")
    private Pizzeria pizzeria;
}
```

### One-to-Many / Many-to-One
- Estas relaciones se utilizan cuando una entidad está asociada con múltiples instancias de otra entidad, y la entidad relacionada conoce a su "padre".
- **Ejemplo en la app de Pizzería:** Una **Pizzería** tiene múltiples **Órdenes**, pero una **Orden** está relacionada con una sola **Pizzería**.

```java
@Entity
public class Pizzeria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "pizzeria") //, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pizzeria_id", referencedColumnName = "id")
    private Pizzeria pizzeria;
}
```

### Many-to-Many
- Estas relaciones son usadas cuando varias instancias de una entidad están asociadas con varias instancias de otra entidad.
- **Ejemplo en la app de Pizzería:** Una **Orden** puede tener varias **Pizzas**, y una **Pizza** puede estar en varias **Órdenes**.

```java
@Entity
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "pizzas")
    private List<Order> orders = new ArrayList<>();
}

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "order_pizza",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "pizza_id")
    )
    private List<Pizza> pizzas = new ArrayList<>();
}
```

### Implementación de un CommandLineRunner

En el siguiente ejemplo, implementamos un `CommandLineRunner` para poblar y probar las relaciones.

```java
@Component
public class PizzeriaCommandLineRunner implements CommandLineRunner {

    @Autowired
    private PizzeriaRepository pizzeriaRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear una Pizzería
        Pizzeria pizzeria = new Pizzeria();
        pizzeria.setName("Pizzeria Bella Napoli");

        // Crear un empleado (localManager) y guardarlo explícitamente
        Employee manager = new Employee();
        manager.setName("Mario Rossi");
        employeeRepository.save(manager);  // Save the Employee first

        // Asociar el empleado con la pizzería (sin cascade)
        pizzeria.setLocalManager(manager); // Set the manager in Pizzeria

        // Guardar la Pizzería (ahora que el manager está guardado)
        pizzeriaRepository.save(pizzeria);

        // Crear algunas Pizzas y guardarlas explícitamente
        Pizza margherita = new Pizza();
        margherita.setName("Margherita");
        Pizza pepperoni = new Pizza();
        pepperoni.setName("Pepperoni");

        pizzaRepository.save(margherita);  // Save Pizza first
        pizzaRepository.save(pepperoni);   // Save Pizza first

        // Crear una Orden y asociarla con la Pizzería y las Pizzas
        Order order1 = new Order();
        order1.setPizzeria(pizzeria);  // Set the Pizzeria for the order
        order1.getPizzas().add(margherita); // Add Pizzas to the order
        order1.getPizzas().add(pepperoni);

        // Guardar la Orden explícitamente antes de asociarla a la Pizzería
        orderRepository.save(order1);  // Save the Order

        // Relacionar la Orden con la Pizzería (añadir a la lista de orders)
        pizzeria.getOrders().add(order1);

        // Volver a guardar la Pizzería con la orden asociada
        pizzeriaRepository.save(pizzeria);
    }
}

```


### Eager y Lazy Fetching

#### **Eager Fetching:**
- En **Eager Loading** o carga temprana/ansiosa, cuando una entidad se carga, todas las entidades relacionadas también se cargan inmediatamente. Esto es útil cuando sabemos que siempre vamos a necesitar la información relacionada, pero puede afectar el rendimiento si hay muchas relaciones o grandes volúmenes de datos.
- **Ejemplo:** Si queremos cargar siempre el **localManager** (el empleado encargado de la pizzería) junto con la **Pizzería**, podemos usar **Eager Fetching**.
- `OneToOne` y `ManyToOne` tienen, por defecto, la estrategia de **carga temprana**

```java
@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
@JoinColumn(name = "local_manager_id", referencedColumnName = "id")
private Employee localManager;
```

En este caso, cada vez que carguemos una **Pizzería**, su **localManager** también se cargará inmediatamente, sin importar si lo necesitamos o no.

#### **Lazy Fetching:**
- En **Lazy Loading** carga diferida/perezosa, las entidades relacionadas no se cargan inmediatamente, sino cuando se accede a ellas por primera vez. Esto es útil cuando no siempre necesitamos cargar los datos relacionados, lo que mejora el rendimiento.
- **Ejemplo:** Si no queremos cargar todas las **Órdenes** asociadas con una **Pizzería** de inmediato, podemos definir que la colección de **Órdenes** se cargue de forma **Lazy**.
- `OneToMany` y `ManyToMany` tienen, por defecto, la estrategia de **carga diferida**

```java
@OneToMany(mappedBy = "pizzeria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
private List<Order> orders = new ArrayList<>();
```

En este caso, las **Órdenes** solo se cargarán cuando llamemos a `pizzeria.getOrders()` por primera vez.

### Uso de CascadeType

El **CascadeType** en JPA especifica cómo las operaciones realizadas en una entidad se propagan a las entidades relacionadas. Los tipos de **Cascade** más comunes son:

- **CascadeType.ALL**: Propaga todas las operaciones (save, delete, update, etc.) desde la entidad padre a la entidad hija.
- **CascadeType.PERSIST**: Propaga la operación de guardado.
- **CascadeType.REMOVE**: Propaga la operación de eliminación.

#### **Ejemplo con CascadeType:**

Si usamos `CascadeType.ALL` en la relación entre **Pizzería** y **localManager**, al guardar una **Pizzería**, automáticamente se guardará su **localManager**. De manera similar, si eliminamos la **Pizzería**, se eliminará también el **localManager**.

```java
@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
@JoinColumn(name = "local_manager_id", referencedColumnName = "id")
private Employee localManager;
```

### Implementación en el CommandLineRunner con Lazy y Eager Loading

Aquí tienes una implementación extendida del `CommandLineRunner` que muestra cómo funcionan **Lazy** y **Eager Loading**, junto con el uso de **CascadeType.ALL** para propagar las operaciones entre entidades.

```java
@Component
public class PizzeriaDataLoader implements CommandLineRunner {

    @Autowired
    private PizzeriaRepository pizzeriaRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear una Pizzería
        Pizzeria pizzeria = new Pizzeria();
        pizzeria.setName("Pizzeria Bella Napoli");

        // Crear un empleado (localManager)
        Employee manager = new Employee();
        manager.setName("Mario Rossi");
        pizzeria.setLocalManager(manager); // CascadeType.ALL asegura que el manager se guarda junto a la pizzería

        // Crear algunas Pizzas
        Pizza margherita = new Pizza();
        margherita.setName("Margherita");

        Pizza pepperoni = new Pizza();
        pepperoni.setName("Pepperoni");

        pizzaRepository.save(margherita);
        pizzaRepository.save(pepperoni);

        // Crear una Orden
        Order order1 = new Order();
        order1.setPizzeria(pizzeria); // La Orden está asociada a la Pizzería (Lazy Loading)
        order1.getPizzas().add(margherita);
        order1.getPizzas().add(pepperoni);

        // Relacionar la Orden con la Pizzería
        pizzeria.getOrders().add(order1);

        // Guardar todo
        pizzeriaRepository.save(pizzeria);

        // --- Ejemplo de Lazy Loading ---
        // No se cargan las órdenes hasta que las accedemos
        Pizzeria loadedPizzeria = pizzeriaRepository.findById(pizzeria.getId()).orElseThrow();
        System.out.println("Pizzeria cargada: " + loadedPizzeria.getName());

        // Ahora, accediendo a las órdenes se desencadena la carga
        System.out.println("Accediendo a las órdenes de la pizzería (Lazy Loading): ");
        loadedPizzeria.getOrders().forEach(order -> {
            System.out.println("Orden ID: " + order.getId() + " - Pizzas: " + order.getPizzas().size());
        });

        // --- Ejemplo de Eager Loading ---
        // El localManager se carga inmediatamente al cargar la pizzería (Eager Loading)
        System.out.println("Manager de la pizzería cargado con Eager Loading: " + loadedPizzeria.getLocalManager().getName());
    }
}
```

### Explicación del Código

1. **CascadeType.ALL**:
    - En la relación `Pizzeria -> Employee`, hemos usado **CascadeType.ALL** para asegurarnos de que las operaciones realizadas sobre la **Pizzería** se propaguen al **localManager**. En este caso, al guardar una pizzería, también guardamos el empleado.

2. **Lazy Loading**:
    - Hemos definido que la relación **Pizzeria -> Orders** sea **Lazy** (`fetch = FetchType.LAZY`). Esto significa que cuando cargamos la **Pizzería**, las **Órdenes** no se cargan hasta que accedemos a ellas explícitamente con `pizzeria.getOrders()`.

3. **Eager Loading**:
    - Por otro lado, la relación **Pizzeria -> Employee** está configurada como **Eager** (`fetch = FetchType.EAGER`), lo que significa que cuando cargamos la **Pizzería**, el **localManager** se carga automáticamente.

### Resultado esperado al ejecutar el CommandLineRunner

Cuando se ejecuta este código, primero se carga la **Pizzería**, y su **localManager** se cargará automáticamente debido a **Eager Loading**. Sin embargo, las **Órdenes** no se cargarán hasta que llamemos a `pizzeria.getOrders()`, lo que demostrará el comportamiento de **Lazy Loading**.

El uso de **CascadeType.ALL** asegura que las operaciones de guardar y eliminar en la **Pizzería** se propaguen automáticamente al **localManager** y las **Órdenes**, sin necesidad de gestionar manualmente cada una de las entidades relacionadas.

---

## Ejercicio: Ampliando la Aplicación de Pizzería con Nuevas Relaciones

### Instrucciones

1. **Crea la entidad `Ingredient`:**
    - Representa los ingredientes que puede tener una pizza.
    - Relaciona esta entidad con la entidad `Pizza` de forma **Many-to-Many**.

2. **Crea la entidad `Supplier`:**
    - Representa los proveedores de ingredientes.
    - Relaciona esta entidad con la entidad `Ingredient` de forma **One-to-Many** y **Many-to-One** (un proveedor tiene varios ingredientes, pero un ingrediente tiene un único proveedor).

3. **Crea la entidad `Delivery`:**
    - Representa las entregas asociadas a las órdenes.
    - Relaciona esta entidad con la entidad `Order` de forma **One-to-One** (una entrega por orden, y una orden por entrega).

4. **Relaciones a implementar:**
    - **Many-to-Many** entre `Pizza` y `Ingredient`.
    - **One-to-Many** y **Many-to-One** entre `Ingredient` y `Supplier`.
    - **One-to-One** entre `Order` y `Delivery`.

5. **Implementa un `CommandLineRunner`:**
    - Crea una instancia de `Pizzeria` con algunas `Pizzas`, `Orders`, `Ingredients`, `Suppliers` y `Deliveries`, demostrando el uso de todas estas relaciones.
    - Asegúrate de guardar todas las entidades y probar las relaciones definidas.

#### Requerimientos

- Usa las relaciones **One-to-One**, **One-to-Many**, **Many-to-One**, y **Many-to-Many** correctamente.
- Configura las operaciones de **Cascade** según sea necesario.
- Prueba las cargas **Eager** y **Lazy** en algunas de las relaciones.
