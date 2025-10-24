package com.migramer.store.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {

    @NotBlank(message = "El campo email es obligatorio")
    private String email;

    @NotBlank(message = "El campo password es obligatorio")
    private String password;
}