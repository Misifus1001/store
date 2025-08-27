package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    
}
