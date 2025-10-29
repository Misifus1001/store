package com.migramer.store.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperarPaswordRequest {
    @NotBlank(message = "El campo 'email' es **obligatorio**.")
    @Email(message = "El valor de 'email' debe ser una **dirección de correo electrónico válida**.")
    private String email;
}