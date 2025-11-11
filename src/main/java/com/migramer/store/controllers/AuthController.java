package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.migramer.store.models.RecuperarPasswordResponse;
import com.migramer.store.models.RecuperarPaswordRequest;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody TokenRequest tokenRequest) {
        return ResponseEntity.ok(usuarioService.validarUsuario(tokenRequest));
    }

    @PostMapping("/reestablecer-password")
    public ResponseEntity<RecuperarPasswordResponse> restablecerPassword(@Valid @RequestBody RecuperarPaswordRequest recuperarPaswordRequest) {
        RecuperarPasswordResponse recuperarPasswordResponse = usuarioService.callReestablecerPassword(recuperarPaswordRequest);
        return ResponseEntity.ok(recuperarPasswordResponse);
    }

}