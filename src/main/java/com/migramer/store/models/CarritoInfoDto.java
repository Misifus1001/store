package com.migramer.store.models;

import java.util.List;

import lombok.Data;

@Data
public class CarritoInfoDto {
    
    private Info info;
    private List<CarritoDto> items;
    
}