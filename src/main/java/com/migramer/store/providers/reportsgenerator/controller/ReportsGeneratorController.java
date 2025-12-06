package com.migramer.store.providers.reportsgenerator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.providers.reportsgenerator.model.ReporteVentasResponse;
import com.migramer.store.providers.reportsgenerator.service.ReportsGeneratorService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ventas")
public class ReportsGeneratorController {

    private final ReportsGeneratorService reportsGeneratorService;

    public ReportsGeneratorController(ReportsGeneratorService reportsGeneratorService){
        this.reportsGeneratorService = reportsGeneratorService;
    }

    @GetMapping
    public ReporteVentasResponse getReporteVentas() {
        return null;
    }
    
}