package com.ironhack.week7saturday.repository;

import com.ironhack.week7saturday.model.Pizzeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzeriaRepository extends JpaRepository<Pizzeria, Long> {
    List<Pizzeria> findAllByLocalManger_Name(String localManagerName);
}
