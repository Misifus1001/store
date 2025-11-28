package com.migramer.store.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddToCarritoRequest {

    @NotBlank(message = "El campo código de barras es obligatorio")
    @Size(min = 8, max = 20, message = "El código de barras debe tener entre {min} y {max} caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El código de barras solo debe contener números")
    private String codigoBarras;

    @NotNull(message = "El campo stock es obligatorio")
    @Min(value = 1, message = "El stock no puede ser negativo ni menor a 0")
    private Integer cantidad;

    // @NotBlank(message = "El UUID de la tienda es obligatorio")
    // @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "El formato del UUID de la tienda es incorrecto")
    // private String uuidTienda;
    
}