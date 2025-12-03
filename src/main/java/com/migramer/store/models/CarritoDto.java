package com.migramer.store.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CarritoDto {
    private Integer id;
    private Integer cantidad;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String urlImagen;
    
}
