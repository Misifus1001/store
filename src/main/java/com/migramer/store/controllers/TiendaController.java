package com.migramer.store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.TiendaDto;
import com.migramer.store.service.TiendaService;
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
    public TiendaDto registrarTienda(@RequestBody TiendaDto tiendaDto) {

        return tiendaService.save(tiendaDto);
    }

    @GetMapping
    public List<TiendaDto> getTiendas() {
        return tiendaService.getTiendasDto();
    }

    @GetMapping("/{id}")
    public TiendaDto getTiendaById(@PathVariable("id") Integer id) {
        return tiendaService.getTiendaById(id);
    }
    
    
}
