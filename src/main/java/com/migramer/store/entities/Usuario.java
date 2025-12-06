package com.migramer.store.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean estatus;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "fk_rol", nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "fk_tienda", nullable = true)
    private Tienda tienda;

    @OneToMany(mappedBy = "usuarioForCarrito", fetch = FetchType.EAGER)
    private List<Carrito> carritoList;

    @OneToOne(mappedBy = "usuarioToken")
    private FirebaseEntityToken firebaseEntityToken;
}