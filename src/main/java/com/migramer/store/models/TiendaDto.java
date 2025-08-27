package com.migramer.store.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TiendaDto {

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String nombre;

    @NotBlank(message = "La ubicacion es obligatoria")
    private String ubicacion;
    
}