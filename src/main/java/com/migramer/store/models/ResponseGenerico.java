package com.migramer.store.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGenerico <T>{
    
    private String message;
    private String estatus;
    private T data;
}