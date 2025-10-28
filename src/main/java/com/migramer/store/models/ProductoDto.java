package com.migramer.store.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDto {
    
    private Integer id;
 
    @NotBlank(message = "El campo código de barras es obligatorio")
    @Size(min = 8, max = 20, message = "El código de barras debe tener entre {min} y {max} caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El código de barras solo debe contener números")
    private String codigoBarras;

    @NotBlank(message = "El campo descripción es obligatorio")
    @Size(max = 255, message = "La descripción no debe exceder los {max} caracteres")
    private String descripcion;

    @NotNull(message = "El campo precio es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser un valor positivo (mínimo 0.01)")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener como máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "El campo stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    private String urlImagen;
    private Boolean estatus;
    private LocalDateTime fechaCreacion;

    @NotBlank(message = "El UUID de la tienda es obligatorio")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "El formato del UUID de la tienda es incorrecto")
    private String uuidTienda;
}