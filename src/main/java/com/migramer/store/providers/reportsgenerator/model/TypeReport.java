package com.migramer.store.providers.reportsgenerator.model;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeReport {
    
    PDF("Se necesita PDF en base64"),
    IMAGE("Se necesita Imagen en base64");
    private String type;

}