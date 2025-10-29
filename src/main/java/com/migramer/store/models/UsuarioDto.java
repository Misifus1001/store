package com.migramer.store.models;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class UsuarioDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    private Integer idTienda;
}