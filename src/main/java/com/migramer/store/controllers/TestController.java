package com.migramer.store.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public/saludar")
    public String saludar() {
        return "Hola :)";
    }

    @PostMapping("/api/push-notification")
    public String pushNotification(@RequestBody Map<String, Object> payload) {

        System.out.println(payload);
        
        return "Hola :)";
    }

    @GetMapping("/protegida/hello")
    public String holaConSeguridadJaja() {
        return "Hola esta cosa tiene seguridad ('-')";
    }
    
}