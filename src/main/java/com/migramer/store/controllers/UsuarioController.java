package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.ChangePasswordRequest;
import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UsuarioDto;
import com.migramer.store.security.CustomUserDetails;
import com.migramer.store.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/registrar/vendedor")
    public ResponseEntity<TokenResponse> registrarVendedor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuarioDto, "VENDEDOR", userDetails.getUuidTienda()));
    }

    @PostMapping("/registrar/propietario")
    public ResponseEntity<TokenResponse> registrarPropietario(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity
                .ok(usuarioService.registrarUsuario(usuarioDto, "PROPIETARIO", userDetails.getUuidTienda()));
    }

    @GetMapping("/empleados")
    public PaginacionResponse getEmpleados(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return usuarioService.getEmpleadosByTienda(page, size, userDetails.getUuidTienda());
    }

    @GetMapping("/propietarios")
    public PaginacionResponse getPropietarios(
            @RequestParam String uuidTienda,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return usuarioService.getAllUsersByTienda(page, size, uuidTienda);
    }

}
