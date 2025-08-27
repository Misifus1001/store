package com.migramer.store.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tienda")
@Data
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String ubicacion;
    private Boolean estatus;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "tiendaForProducto", fetch = FetchType.EAGER)
    private List<Producto> productoList;

    @OneToMany(mappedBy = "tiendaForUsuario", fetch = FetchType.EAGER)
    private List<Usuario> usuarioList;
    
}