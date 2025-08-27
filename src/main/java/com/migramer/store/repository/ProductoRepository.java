package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer>{
    
}
