package com.migramer.store.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private String token;
    private String rol;
    private Integer tiendaId;
    private String uuidTienda;
    private String nombre;
}