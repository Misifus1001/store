package com.migramer.store.providers.emailprovider.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeHtmlBody {

    RESET_PASSWORD("HTML para recuperar contraseña"),
    SEND_WELCOME("HTML para dar la bienvenida");

    String descripcion;
}