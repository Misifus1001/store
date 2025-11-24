package com.migramer.store.providers.reportsgenerator.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JasperFillType {

    REPORT_WITH_PARAMETERS_ONLY("Reporte que se llena unicamente con parametros"),
    REPORT_WITH_JSON_AND_PARAMETERS("Reporte que se llena con json y parametros");
    private String descripcion;
    
}