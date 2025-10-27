package com.migramer.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Producto;
import com.migramer.store.entities.Tienda;


public interface ProductoRepository extends JpaRepository<Producto, Integer>{

    Page<Producto> findAllByTiendaForProducto(Tienda tiendaForProducto, Pageable pageable);

}