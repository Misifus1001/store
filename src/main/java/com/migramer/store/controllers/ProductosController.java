package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.ProductoDto;
import com.migramer.store.service.ProductosService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @PreAuthorize("hasRole('PROPIETARIO') or hasRole('ADMIN')")
    @PostMapping("/guardar")
    public ResponseEntity<ProductoDto> saveProductoDto(@Valid @RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productosService.saveProductoDto(productoDto));
    }

    @GetMapping
    public PaginacionResponse getProductsByTienda(
            @RequestParam String uuidTienda,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return productosService.getProductsByPageAndTienda(uuidTienda, page, size);
    }

    @GetMapping("/search")
    public ProductoDto getProductByBarcodeAndUuidTienda(
            @RequestParam String uuidTienda,
            @RequestParam String barcode) {
        return productosService.getProductByBarcodeAndUuidTienda(uuidTienda, barcode);
    }

}