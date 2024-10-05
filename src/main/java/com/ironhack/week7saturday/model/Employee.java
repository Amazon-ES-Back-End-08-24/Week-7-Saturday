package com.ironhack.week7saturday.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String name;

    // si pongo esto es una relación bidireccional
    @OneToOne(mappedBy = "localManger") //, fetch = FetchType.EAGER)
    private Pizzeria pizzeria;
}
