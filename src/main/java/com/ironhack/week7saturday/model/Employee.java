package com.ironhack.week7saturday.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String name;

    // si pongo esto es una relación bidireccional
    @OneToOne(mappedBy = "localManger")
    private Pizzeria pizzeria;
}
