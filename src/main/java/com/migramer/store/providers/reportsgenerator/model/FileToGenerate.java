package com.migramer.store.providers.reportsgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileToGenerate {
    private JasperFillType jasperFillType;
    private TypeReport typeReport;
}