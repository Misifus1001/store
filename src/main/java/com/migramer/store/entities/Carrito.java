package com.migramer.store.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "carrito")
@Data
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Boolean estatus;
    private Integer cantidad;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_etapa", referencedColumnName = "id")
    private Etapa etapaForCarrito;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_venta", referencedColumnName = "id")
    private Ventas ventasForCarrito;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_producto")
    private Producto productoForCarrito;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_usuario")
    private Usuario usuarioForCarrito;

    // @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    // @JoinColumn(name = "fk_producto")
    // private Producto productoForCarrito;

    // @OneToMany(mappedBy = "carritoForProductoCarrito", fetch = FetchType.EAGER)
    // private List<ProductoCarrito> productoCarritoList;

}