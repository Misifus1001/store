package com.migramer.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
    
}