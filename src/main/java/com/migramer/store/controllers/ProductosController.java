package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.service.ProductosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @GetMapping
    public String getMethodName() {

        productosService.probarGuardado();
        return "asdasd";
    }

    @GetMapping("/search")
    public PaginacionResponse getProductsByTienda(
            @RequestParam Integer idTienda,
            @RequestParam(required = false, defaultValue = "0") Integer page, 
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return productosService.probarVisualizacion(idTienda, page, size);
    }

}