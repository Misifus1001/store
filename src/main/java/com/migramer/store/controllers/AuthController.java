package com.migramer.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.ResponseGenerico;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UserDto;
import com.migramer.store.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/registrarUsuario")
    public ResponseEntity<ResponseGenerico<Object>> registrarUsuario(@Valid @RequestBody UserDto userDto) {

        ResponseGenerico<Object> responseGenerico = new ResponseGenerico<>();

        TokenResponse tokenResponse = new TokenResponse();

        String token = userService.registrarUsuario(userDto);
        tokenResponse.setToken(token);

        // responseGenerico.setEstatus(HttpStatus.CREATED.getReasonPhrase());
        // responseGenerico.setMessage(String.valueOf(HttpStatus.CREATED.value()));
        responseGenerico.setData(tokenResponse);

        return ResponseEntity.status(200).body(responseGenerico);

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseGenerico<Object>> login(@Valid @RequestBody TokenRequest tokenRequest) {

        ResponseGenerico<Object> responseGenerico = new ResponseGenerico<>();

        TokenResponse tokenResponse = new TokenResponse();
        String token = userService.validarUsuario(tokenRequest.getEmail(), tokenRequest.getPassword());
        tokenResponse.setToken(token);

        responseGenerico.setEstatus(HttpStatus.OK.getReasonPhrase());
        responseGenerico.setMessage(String.valueOf(HttpStatus.OK.value()));
        responseGenerico.setData(tokenResponse);

        return ResponseEntity.status(HttpStatus.valueOf(responseGenerico.getEstatus())).body(responseGenerico);

    }

}