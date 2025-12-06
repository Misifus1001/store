package com.migramer.store.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migramer.store.entities.Carrito;
import com.migramer.store.entities.Producto;

import com.migramer.store.entities.Usuario;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Carrito findByProductoForCarrito(Producto productoForCarrito);

    Optional<Carrito> findTop1ByProductoForCarritoAndUsuarioForCarrito(Producto productoForCarrito,Usuario usuarioForCarrito);

    Page<Carrito> findAllByUsuarioForCarrito(Usuario usuarioForCarrito, Pageable pageable);

    @Query("SELECT SUM(c.cantidad * p.precio) FROM Carrito c JOIN c.productoForCarrito p WHERE c.usuarioForCarrito = :usuario")
    BigDecimal getTotalPagarByUsuario(@Param("usuario") Usuario usuario);

}
