package com.migramer.store.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "producto_carrito")
@Data
public class ProductoCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cantidad;
    private Boolean estatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_carrito")
    private Carrito carritoForProductoCarrito;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_producto")
    private Producto productoForProductoCarrito;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
}