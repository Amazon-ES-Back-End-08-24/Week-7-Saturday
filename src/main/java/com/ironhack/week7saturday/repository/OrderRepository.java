package com.ironhack.week7saturday.repository;

import com.ironhack.week7saturday.model.Order;
import com.ironhack.week7saturday.model.Pizzeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByPizzeria_PizzeriaId(Long pizzeriaId);
    List<Order> findAllByPizzeria_Name(String pizzeriaName);
}