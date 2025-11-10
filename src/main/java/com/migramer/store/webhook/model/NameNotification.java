package com.migramer.store.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NameNotification {

    USUARIO("users"),
    TIENDA("tiendas"),
    VENTAS("ventas");

    String descripcion;
    
}