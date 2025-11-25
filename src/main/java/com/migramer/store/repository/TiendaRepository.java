package com.migramer.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.Tienda;

public interface TiendaRepository extends JpaRepository<Tienda, Integer>{

    Page<Tienda> findAll(Pageable pageable);

    Tienda findByUuid(String uuid);
    
}
