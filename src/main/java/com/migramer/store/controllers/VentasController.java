package com.migramer.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.MessageResponse;
import com.migramer.store.providers.reportsgenerator.model.ResponseReport;
import com.migramer.store.security.CustomUserDetails;
import com.migramer.store.service.VentasService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/ventas")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    @PostMapping("/finalizar-venta")
    public MessageResponse finalizarVenta(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ventasService.generarVenta(userDetails.getUsername());
    }

    @GetMapping
    public ResponseReport getReporteVentas() {
        return ventasService.getReporteVentas();
    }
    

}
