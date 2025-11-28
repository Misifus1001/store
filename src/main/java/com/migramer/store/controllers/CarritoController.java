package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.AddToCarritoRequest;
import com.migramer.store.models.MessageResponse;
import com.migramer.store.service.CarritoService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PostMapping("/add-product")
    public ResponseEntity<MessageResponse> addProductoToShoppingCart(@Valid @RequestBody AddToCarritoRequest addToCarritoRequest){
        MessageResponse messageResponse = carritoService.addProductToShoppingCart(addToCarritoRequest);
        return ResponseEntity.ok(messageResponse);
    } 
    
}