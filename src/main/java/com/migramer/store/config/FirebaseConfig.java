package com.migramer.store.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config}")
    private String firebaseCredentialsJson;

    @PostConstruct
    public void initFirebase() throws Exception {
        
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream credentialsStream = new ByteArrayInputStream(firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase inicializado");
        }
    }
}
