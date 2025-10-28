package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.EmailRequest;
import com.migramer.store.models.EmailResponse;
import com.migramer.store.providers.EmailProvider;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/providers")
public class EmailProviderController {

    @Autowired
    private EmailProvider emailProvider;

    @PostMapping("/send-email")
    public ResponseEntity<EmailResponse> sendemail(@Valid @RequestBody EmailRequest emailRequest) {
        EmailResponse emailResponse = emailProvider.sendImageEmail(emailRequest);
        return ResponseEntity.ok(emailResponse);
    }

}
