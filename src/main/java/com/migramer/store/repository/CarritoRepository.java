package com.migramer.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Carrito;
import com.migramer.store.entities.Producto;

import com.migramer.store.entities.Usuario;
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Carrito findByProductoForCarrito(Producto productoForCarrito);

    Optional<Carrito> findTop1ByProductoForCarritoAndUsuarioForCarrito(Producto productoForCarrito, Usuario usuarioForCarrito);
    
}
