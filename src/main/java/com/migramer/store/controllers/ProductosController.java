package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.ProductoDto;
import com.migramer.store.security.CustomUserDetails;
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

    @PostMapping("/guardar")
    public ResponseEntity<ProductoDto> saveProductoDto(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productosService.saveProductoDto(productoDto, userDetails.getUuidTienda()));
    }

    @GetMapping
    public PaginacionResponse getProductsByTienda(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return productosService.getProductsByPageAndTienda(userDetails.getUuidTienda(), page, size);
    }

    @GetMapping("/search")
    public ProductoDto getProductByBarcodeAndUuidTienda(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String barcode) {
        return productosService.getProductByBarcodeAndUuidTienda(userDetails.getUuidTienda(), barcode);
    }

}