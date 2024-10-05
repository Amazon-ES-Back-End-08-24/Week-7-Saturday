package com.ironhack.week7saturday.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pizzas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // si pongo esto es una relación bidireccional
    @ManyToMany(mappedBy = "pizzas")
    private List<Order> orders = new ArrayList<>();
}
