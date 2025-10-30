package com.migramer.store.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.providers.NotificationService;

@RestController
@RequestMapping("/api/supabase")
public class SupabaseWebhookController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {

        System.out.println("Cambio detectado desde Supabase: " + payload);

        notificationService.broadcastChange(payload);

        return ResponseEntity.ok("OK");
    }

}
