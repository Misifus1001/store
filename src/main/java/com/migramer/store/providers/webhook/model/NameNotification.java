package com.migramer.store.providers.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NameNotification {

    USUARIO("users"),
    PRODUCTOS("productos"),
    TIENDA("tiendas"),
    VENTAS("ventas");

    String descripcion;
    
}