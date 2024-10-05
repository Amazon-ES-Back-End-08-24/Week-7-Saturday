package com.ironhack.week7saturday.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pizzerias")
public class Pizzeria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pizzeriaId;

    private String name;

    @OneToOne
    @JoinColumn(name = "local_manager_id", referencedColumnName = "employeeId") // no obligatorio
    private Employee localManger;

    // si pongo esto es una relación bidireccional
    @OneToMany(mappedBy = "pizzeria")
    private List<Order> orders = new ArrayList<>();
}

// porque inicializamos la lista de orders
// orders.add(order);
// habría que hacer antes -> orders = new ArrayList<>();
