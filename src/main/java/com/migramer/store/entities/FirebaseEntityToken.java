package com.migramer.store.entities;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "firebase_entity_token")
@Data
public class FirebaseEntityToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id")
    private Usuario usuarioToken;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private Boolean activo;

}