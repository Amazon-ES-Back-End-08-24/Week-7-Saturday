package com.ironhack.week7saturday;

import com.ironhack.week7saturday.model.Employee;
import com.ironhack.week7saturday.model.Order;
import com.ironhack.week7saturday.model.Pizza;
import com.ironhack.week7saturday.model.Pizzeria;
import com.ironhack.week7saturday.repository.EmployeeRepository;
import com.ironhack.week7saturday.repository.OrderRepository;
import com.ironhack.week7saturday.repository.PizzaRepository;
import com.ironhack.week7saturday.repository.PizzeriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PizzeriaCommandLineRunner implements CommandLineRunner {

    @Autowired
    private PizzeriaRepository pizzeriaRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public void run(String... args) throws Exception {
        Pizzeria pizzeria = new Pizzeria();
        pizzeria.setName("Pizzeria Bella Napoli");

        Employee employee = new Employee();
        employee.setName("Mario Rossi");

        pizzeria.setLocalManger(employee);
        System.out.println("Pizzeria ID " + pizzeria.getPizzeriaId());

        Pizzeria savedPizzeria = pizzeriaRepository.save(pizzeria);
        System.out.println("Pizzeria ID " + savedPizzeria.getPizzeriaId());

        // cómo sería sin el cascade ALL
        // 1. Crear y guardar la pizzeria
        // 2. Crear y guardar el employee (localManager luego)
        // 3. Set localManager de la pizzeria
        // 4. Actualizar (.save()) la pizzeria

        Pizza margherita = new Pizza();
        margherita.setName("Margherita");

        Pizza pepperoni = new Pizza();
        pepperoni.setName("Pepperoni");

        pizzaRepository.save(margherita);
        pizzaRepository.save(pepperoni);

        Order order = new Order();
        order.setPizzeria(pizzeria);
        List<Pizza> pizzas = order.getPizzas();
        pizzas.add(pepperoni);
        pizzas.add(margherita);
        // orderRepository.save(order); --> si no tiene el cascade ALL en Pizzeria

        List<Order> pizzeriaOrders = pizzeria.getOrders();
        pizzeriaOrders.add(order);
        pizzeriaRepository.save(pizzeria);


        Optional<Pizzeria> optionalPizzeria = pizzeriaRepository.findById(savedPizzeria.getPizzeriaId());

        if (optionalPizzeria.isEmpty()) {
            System.out.println("Error, not found Pizzeria with ID " + savedPizzeria.getPizzeriaId());
        } else {
            Pizzeria pizzeriaFromDB = optionalPizzeria.get();

            // Descomentar para probar el LAZY / EAGER
//            System.out.println("Pizzeria order list: ");
//            List<Order> pizzeriaOrderList = pizzeriaFromDB.getOrders();
//            for (Order order1 : pizzeriaOrderList) {
//                System.out.println("Order from pizzeria order list " + order1.getOrderId());
//                System.out.println("Amount of pizzas from Order from Pizzeria order list " + order1.getPizzas().size());
//            }

            System.out.println("Pizzeria local manager " + pizzeriaFromDB.getLocalManger().getName());
        }


        List<Order> ordersByPizzeriaId = orderRepository.findAllByPizzeria_PizzeriaId(savedPizzeria.getPizzeriaId());

        for (Order order1 : ordersByPizzeriaId) {
            System.out.println("Order from pizzeria order list " + order1.getOrderId());
        }

        // cuando se borre la pizzeria se actualice (borre, etc) todo lo relacionado
        // pizzeriaRepository.deleteById(savedPizzeria.getPizzeriaId());
    }
}
