package com.migramer.store.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "El campo nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El campo correo es obligatorio")
    private String correo;

    @NotBlank(message = "El campo password es obligatorio")
    private String password;
}