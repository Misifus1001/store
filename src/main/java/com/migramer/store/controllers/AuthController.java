package com.migramer.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.entities.User;
import com.migramer.store.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/registrarUsuario")
    public Map<String, Object> registrarUsuario(@RequestBody User user) {

        String token = userService.registrarUsuario(user);
        
        return Map.of("token",token);
    }


    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String token = userService.autenticar(request.get("email"), request.get("password"));
        
        return Map.of("token", token);
    }
    
    
}