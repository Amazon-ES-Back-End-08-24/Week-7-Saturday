package com.ironhack.week7saturday.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pizzas")
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // si pongo esto es una relación bidireccional
    @ManyToMany(mappedBy = "pizzas")
    private List<Order> orders = new ArrayList<>();
}
