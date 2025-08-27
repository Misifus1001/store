package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
}
