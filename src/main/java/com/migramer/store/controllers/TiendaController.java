package com.migramer.store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.ResponseGenerico;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseGenerico<Object>> registrarTienda(@Valid @RequestBody TiendaDto tiendaDto) {

        try {
            TiendaDto tiendaDtoSave = tiendaService.save(tiendaDto);

            ResponseGenerico<Object> responseGenerico = new ResponseGenerico<Object>(
                HttpStatus.CREATED.getReasonPhrase(),
                String.valueOf(HttpStatus.CREATED.value()), 
                tiendaDtoSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseGenerico);

        } catch (Exception e) {
            ResponseGenerico<Object> responseGenerico = new ResponseGenerico<Object>(
                    "Error al guardar la tienda",
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseGenerico);
        }
    }

    @GetMapping
    public List<TiendaDto> getTiendas() {
        return tiendaService.getTiendasDto();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public TiendaDto getTiendaById(@PathVariable("id") Integer id) {
        return tiendaService.getTiendaById(id);
    }
    
    
}
