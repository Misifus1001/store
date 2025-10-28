package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.EmailRequest;
import com.migramer.store.models.EmailResponse;
import com.migramer.store.providers.EmailProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/providers")
public class EmailProviderController {

    @Autowired
    private EmailProvider emailProvider;

    @PostMapping("/send-email")
    public ResponseEntity<EmailResponse> sendemail(@RequestBody EmailRequest emailRequest) {
        EmailResponse emailResponse = emailProvider.sendImageEmail(emailRequest);
        // EmailResponse emailResponse = emailProvider.sendEmail(emailRequest);
        return ResponseEntity.ok(emailResponse);
    }

}
