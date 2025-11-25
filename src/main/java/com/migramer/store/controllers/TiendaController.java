package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.TiendaDto;
import com.migramer.store.service.TiendaService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/tiendas")
public class TiendaController {

    @Autowired
    private TiendaService tiendaService;

    @PostMapping
    public ResponseEntity<TiendaDto> registrarTienda(@Valid @RequestBody TiendaDto tiendaDto) {
        return ResponseEntity.ok(tiendaService.save(tiendaDto));
    }

    @GetMapping
    public PaginacionResponse getTiendas(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return tiendaService.getTiendaByPage(page, size);
    }

    @GetMapping("/{id}")
    public TiendaDto getTiendaById(@PathVariable("id") Integer id) {
        return tiendaService.getTiendaById(id);
    }

}
