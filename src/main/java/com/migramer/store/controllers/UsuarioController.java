package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.ChangePasswordRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UsuarioDto;
import com.migramer.store.security.CustomUserDetails;
import com.migramer.store.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/change-password")
    public ResponseEntity<TokenResponse> saveNewPassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        TokenResponse tokenResponse = usuarioService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @PreAuthorize("hasRole('PROPIETARIO') or hasRole('ADMIN')")
    @PostMapping("/registrar/vendedor")
    public ResponseEntity<TokenResponse> registrarVendedor(
            @Valid @RequestBody UsuarioDto usuarioDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer tiendaIdUsuarioActual = userDetails.getTiendaId();
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuarioDto, "VENDEDOR", tiendaIdUsuarioActual));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrar/propietario")
    public ResponseEntity<TokenResponse> registrarPropietario(
            @Valid @RequestBody UsuarioDto usuarioDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer tiendaIdUsuarioActual = userDetails.getTiendaId();
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuarioDto, "PROPIETARIO", tiendaIdUsuarioActual));
    }
}
