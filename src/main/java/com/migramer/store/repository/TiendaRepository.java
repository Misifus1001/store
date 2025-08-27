package com.migramer.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Tienda;

public interface TiendaRepository extends JpaRepository<Tienda, Integer>{
    
}
