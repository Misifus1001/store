package com.migramer.store.providers.firebase.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseTokenDto {
    @NotBlank(message = "El token del dispositivo no puede estar vacío.")
    private String token;
}