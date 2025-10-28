package com.migramer.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UserDto;
import com.migramer.store.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired 
    private UserService userService;

    @PostMapping("/registrarUsuario")
    public ResponseEntity<TokenResponse> registrarUsuario(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.registrarUsuario(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody TokenRequest tokenRequest) {
        return ResponseEntity.ok(userService.validarUsuario(tokenRequest));
    }

}