package com.ironhack.week7saturday.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pizzerias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pizzeria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pizzeriaId;

    private String name;

    //@OneToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // por defecto -> carga temprana/ansiosa
    // @OneToOne(cascade = CascadeType.ALL) // propaga save, delete, update .. todas las operaciones de Pizzeria
    // @OneToOne(cascade = CascadeType.PERSIST) // propaga el save
    // @OneToOne(cascade = CascadeType.REMOVE) // propaga el delete
    @JoinColumn(name = "local_manager_id", referencedColumnName = "employeeId") // no obligatorio
    private Employee localManger;

    // si pongo esto es una relación bidireccional
    @OneToMany(mappedBy = "pizzeria", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) // por defecto
    //@OneToMany(mappedBy = "pizzeria", fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    // pizzeria.getOrders();
}

// porque inicializamos la lista de orders
// orders.add(order);
// habría que hacer antes -> orders = new ArrayList<>();
