package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UsuarioDto;
import com.migramer.store.security.CustomUserDetails;
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

    @PreAuthorize("hasRole('DUEÑO') or hasRole('ADMIN')")
    @PostMapping("/registrar/vendedor")
    public ResponseEntity<TokenResponse> registrarVendedor(
            @Valid @RequestBody UsuarioDto usuarioDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer tiendaIdUsuarioActual = userDetails.getTiendaId();
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuarioDto, "VENDEDOR", tiendaIdUsuarioActual));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrar/dueño")
    public ResponseEntity<TokenResponse> registrarDueño(
            @Valid @RequestBody UsuarioDto usuarioDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer tiendaIdUsuarioActual = userDetails.getTiendaId();
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuarioDto, "DUEÑO", tiendaIdUsuarioActual));
    }
}