package com.migramer.store.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public/saludar")
    public String saludar() {
        return "Hola :)";
    }

    @GetMapping("/protegida/hello")
    public String holaConSeguridadJaja() {
        return "Hola esta cosa tiene seguridad ('-')";
    }
    
}