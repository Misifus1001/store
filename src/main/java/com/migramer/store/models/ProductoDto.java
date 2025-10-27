package com.migramer.store.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDto {
    private Integer id;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String urlImagen;
    private Boolean estatus;  
    private LocalDateTime fechaCreacion;

}