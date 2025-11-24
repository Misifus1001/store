package com.migramer.store.providers.reportsgenerator.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.migramer.store.providers.reportsgenerator.components.JasperGeneratorService;
import com.migramer.store.providers.reportsgenerator.model.FileToGenerate;
import com.migramer.store.providers.reportsgenerator.model.JasperFillType;
import com.migramer.store.providers.reportsgenerator.model.RequestReporteVentas;
import com.migramer.store.providers.reportsgenerator.model.ResponseReport;
import com.migramer.store.providers.reportsgenerator.model.TypeReport;

@Service
public class ReportsGeneratorService {

    @Autowired
    private JasperGeneratorService jasperGeneratorService;

    private final String nombreCarpeta = "store";

    private final Logger logger = LoggerFactory.getLogger(ReportsGeneratorService.class);

    public ResponseReport generarArchivoReporteVentas(RequestReporteVentas reporteVentas) {

        try {
            String ubicacionReporte = getJasperFileName();
            FileToGenerate fileToGenerate = new FileToGenerate(JasperFillType.REPORT_WITH_PARAMETERS_ONLY, TypeReport.PDF);

            String base64File = jasperGeneratorService.generarArchivoBase64(reporteVentas, ubicacionReporte, null, fileToGenerate);
            return new ResponseReport(base64File);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
            throw new RuntimeException(e);
        }
    }

    private String getJasperFileName() {
        return jasperGeneratorService.generarUrlUbicacionArchivo(nombreCarpeta, "ReporteVentas");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void probar() {

        RequestReporteVentas reporteVentas = new RequestReporteVentas();
        reporteVentas.setMyProperti("algo");
        reporteVentas.setNombreArchivo("archivoTemporal");
        generarArchivoReporteVentas(reporteVentas);
    }

}