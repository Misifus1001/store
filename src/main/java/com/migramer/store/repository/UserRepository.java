package com.migramer.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Usuario;

public interface UserRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario> findByEmail(String email);
    
}