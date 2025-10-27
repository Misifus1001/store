package com.migramer.store.entities;

import java.math.BigDecimal;
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
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "producto")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "codigo_barras")
    private String codigoBarras;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    @Column(name = "url_imagen")
    private String urlImagen;
    private Boolean estatus;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_tienda", nullable =  true, updatable = true)
    private Tienda tiendaForProducto;

    @OneToMany(mappedBy = "productoForProductoCarrito", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductoCarrito> productoCarritoList;
    
}
