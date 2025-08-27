package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    
}
