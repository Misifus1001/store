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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String email;
    private String password;
    // private String descripcion;
    private Boolean estatus;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_rol", nullable = false)
    private Rol rolForUsuario;
    
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_tienda")
    private Tienda tiendaForUsuario;
}