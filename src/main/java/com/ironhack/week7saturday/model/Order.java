package com.ironhack.week7saturday.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
// pedido
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    // many to one

    @ManyToOne
    @JoinColumn(name = "pizzeria_id", referencedColumnName = "pizzeriaId") // no es obligatorio
    private Pizzeria pizzeria;

    @ManyToMany
    @JoinTable(
            name = "pizzas_orders",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_id")
    ) // no es obligatorio
    private List<Pizza> pizzas = new ArrayList<>();
}


// orden (id 1) de Williams -> pizza barbacoa (id 1) y pizza carbonara (id 2)
// orden (id 2) de Álvaro -> pizza carbonara (id 2) y pizza hawaiana (id 3)
// orden (id 3) de Ana -> pizza hawaiana (id 3) y pizza cuatro quesos (id 4)
