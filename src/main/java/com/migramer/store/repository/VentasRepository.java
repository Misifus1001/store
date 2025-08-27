package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Ventas;

public interface VentasRepository extends JpaRepository<Ventas,Integer>{
    
}