package com.migramer.store.providers.reportsgenerator.components;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migramer.store.providers.reportsgenerator.model.FileToGenerate;
import com.migramer.store.providers.reportsgenerator.model.JasperFillType;
import com.migramer.store.providers.reportsgenerator.model.TypeReport;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;

@Component
public class JasperGeneratorService {

    @Value("classpath:/reports")
    private Resource resource;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(JasperGeneratorService.class);

    /**
     * Función que genera un reporte PDF y lo convierte a una imagen
     * (el archivo también puede llenarse con datos JSON)
     * 
     * @param <T> es para indicar que puede recibir cualquier dato
     * @param data Parametros con los que se desea llenar el reporte (espera recibir un objeto)
     * @param sourceReport ubicacion del reporte (ejemplo: /reporte1/reporte1.jrxml)
     * @param jsonString (opcional) json convertido en string (en caso de que se quiera llenar con uno)
     * @param fileToGenerate características del tipo de reporte (Tipo de Llenado y Tipo de Archivo - PDF o Imagen)
     * @return Imagen o PDF en base64
     */
    public <T> String  generarArchivoBase64(T data, String sourceReport, String jsonString, FileToGenerate fileToGenerate) {
            
        Map<String, Object> parameters = objectClassToMap(data,"reporte");

        JasperFillType jasperFillType = fileToGenerate.getJasperFillType();
        TypeReport typeReport = fileToGenerate.getTypeReport();       

        logger.info(jasperFillType.getDescripcion());
        logger.info(typeReport.getType());

        String nombreArchivo = parameters.get("nombreArchivo").toString();

        File pdf = generarPdf(parameters, sourceReport, jsonString, jasperFillType);
        logger.info("File pdf: " + pdf.getName());

        String resultado = null;

        switch (typeReport) {
            case PDF:
                resultado = convertFileToBase64(pdf);
                break;
            case IMAGE:
                File jpgFile = generarJpegDelPdf(nombreArchivo, pdf);
                resultado = convertFileToBase64(jpgFile);
                jpgFile.delete();

                break;
            default:
                throw new RuntimeException("Tipo de documento no configurado");
        }
        pdf.delete();
        return resultado;
    }

    /**
     * Función que genera un reporte temporal de Jasper tipo PDF
     * 
     * @param parameters   Datos con los que se llenará Jasper
     * @param sourceReport Ubicación del archivo
     * @return PDF temporal
     */
    public File generarPdf(Map<String, Object> parameters, String sourceReport, String jsonString,
            JasperFillType jasperFillType) {

        File archivoTemporal = null;

        try {
            Resource resourceCompleted = new UrlResource(resource.getURL().toString() + sourceReport);

            parameters.put("imageDir", "classpath:/static/images/");

            InputStream dataStream = resourceCompleted.getInputStream();
            archivoTemporal = File.createTempFile("" + parameters.get("archivoTemporal"), ".pdf");

            JasperReport jasperReport = JasperCompileManager.compileReport(dataStream);

            JasperPrint jasperPrint = null;

            switch (jasperFillType) {
                case REPORT_WITH_JSON_AND_PARAMETERS:

                    ByteArrayInputStream jsonStream = new ByteArrayInputStream(
                            jsonString.getBytes(StandardCharsets.UTF_8));
                    JsonDataSource dataSource = new JsonDataSource(jsonStream);
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                    break;
                case REPORT_WITH_PARAMETERS_ONLY:
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                    break;
                default:
                    throw new RuntimeException("Tipo de llenado de reporte no configurado");
            }

            JasperExportManager.exportReportToPdfFile(jasperPrint, archivoTemporal.getAbsolutePath());

            logger.info("Reporte creado correctamente");
        } catch (Exception e) {
            logger.error("Error crear reporte:" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("problemas al generar el reporte:" + e.getMessage());
        }
        return archivoTemporal;
    }

    /**
     * Función que convierte PDF a JPG
     * 
     * @param nombreArchivo Nombre que se le asignará a la imagen temporal
     * @param pdfFile       Archivo PDF
     * @return Imagen JPG
     */
    private File generarJpegDelPdf(String nombreArchivo, File pdfFile) {
        File jpgFile = null;
        try {

            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);

            jpgFile = File.createTempFile("" + nombreArchivo, ".jpg");

            ImageIO.write(bufferedImage, "JPG", jpgFile);
        } catch (Exception e) {
            logger.error("Error al convertir PDF a JPEG:" + e.getMessage());
            e.printStackTrace();
        }
        return jpgFile;
    }

    /**
     * Función que convierte una imagen JPG a base64
     * 
     * @param file Archivo
     * @return base64
     */
    public String convertFileToBase64(File file) {
        String base64Encoded = "";

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            base64Encoded = Base64.encodeBase64String(fileContent);

            logger.info("PUNTO DE INTERRUPCIÓN");

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al convertir archivo a Base64");
        }
        return base64Encoded;
    }

    /**
     * Función que genera la ruta donde se encuentra el archivo
     * 
     * @param nombreCarpeta nombre de la carpeta ubicada en resources/reports
     *                      (ejemplo: mireporte)
     * @param nombreArchivo nombre del archivo que se encuentra dentro de dicha
     *                      carpeta (ejemplo: miprimerReporte)
     * @return ubicación del archivo (ejemplo: /mireporte/miprimerReporte.jrxml)
     */
    public String generarUrlUbicacionArchivo(String nombreCarpeta, String nombreArchivo) {

        return "/" + nombreCarpeta + "/" + nombreArchivo + ".jrxml";

    }

    /**
     *  Funcion que recibe cualquier objeto de una clase y lo convierte a un json para que pueda ser procesado por jasper
     * @param <T> Es para indicar que puede recibir cualquier tipo de dato
     * @param data Objeto de la clase
     * @param nombreArchivo Nombre del archivo
     * @return JSON
     */
    public <T> Map<String, Object> objectClassToMap(T data, String nombreArchivo){
        
        try {

            String responseBody = objectMapper.writeValueAsString(data);
			Map<String,Object> map = objectMapper.readValue(responseBody, Map.class);
            map.put("nombreArchivo", nombreArchivo);
            map.put("archivoTemporal", "fileT");
            logger.info(responseBody);
            return map;
        } catch (Exception e) {
            logger.error("ERROR:",e);
            throw new RuntimeException(e.getMessage());
        }
    }

}