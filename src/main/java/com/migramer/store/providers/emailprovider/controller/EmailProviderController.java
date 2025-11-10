package com.migramer.store.providers.emailprovider.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.providers.emailprovider.models.EmailRequest;
import com.migramer.store.providers.emailprovider.models.EmailResponse;
import com.migramer.store.providers.emailprovider.models.TypeHtmlBody;
import com.migramer.store.providers.emailprovider.service.EmailProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/providers")
public class EmailProviderController {

    private final EmailProvider emailProvider;

    public EmailProviderController(EmailProvider emailProvider){
        this.emailProvider = emailProvider;
    }

    @PostMapping("/send-email")
    public ResponseEntity<EmailResponse> sendemail(@Valid @RequestBody EmailRequest emailRequest, TypeHtmlBody typeHtmlBody) {
        EmailResponse emailResponse = emailProvider.sendEmail(emailRequest, typeHtmlBody);
        return ResponseEntity.ok(emailResponse);
    }
    
}
