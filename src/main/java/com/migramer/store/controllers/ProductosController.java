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
    public PaginacionResponse getProductsByTienda(
            @RequestParam String uuidTienda,
            @RequestParam(required = false, defaultValue = "0") Integer page, 
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return productosService.getProductsByPageAndTienda(uuidTienda, page, size);
    }

}