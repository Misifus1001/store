package com.migramer.store.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "El campo email es obligatorio")
    @Email(message = "El valor de 'email' debe ser una dirección de correo electrónico válida.")
    private String email;

    @NotBlank(message = "El campo password es obligatorio")
    private String password;

    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres.")
    @NotBlank(message = "El campo nueva contraseña es obligatorio.")
    private String newPassword;
}