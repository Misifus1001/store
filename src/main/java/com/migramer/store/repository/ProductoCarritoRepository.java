package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.ProductoCarrito;

public interface ProductoCarritoRepository extends JpaRepository<ProductoCarrito, Integer>{
    
}
