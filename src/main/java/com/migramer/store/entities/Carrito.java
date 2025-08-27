package com.migramer.store.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_etapa", referencedColumnName = "id")
    private Etapa etapaForCarrito;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_venta", referencedColumnName = "id")
    private Ventas ventasForCarrito;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "carritoForProductoCarrito", fetch = FetchType.EAGER)
    private List<ProductoCarrito> productoCarritoList;

}