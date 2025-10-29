package com.migramer.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.Rol;
import com.migramer.store.exceptions.ResourceNotFoundException;
import com.migramer.store.repository.RolRepository;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol getRolByName(String rolName) {
        Rol rol = rolRepository.findByNombre(rolName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", rolName));

        return rol;
    }

}