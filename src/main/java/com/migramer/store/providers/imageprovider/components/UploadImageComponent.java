package com.migramer.store.providers.imageprovider.components;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UploadImageComponent {

    private final String UPLOAD_DIR = "uploads/images/";

    private final Logger logger = LoggerFactory.getLogger(UploadImageComponent.class);

    public String uploadImage(String base64, String fileName) {

        try {

            File directory = new File(UPLOAD_DIR);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // String base64Image = base64.split(",")[1];

            byte[] imageBytes = Base64.getDecoder().decode(base64);

            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            Files.write(filePath, imageBytes);

            return fileName;
        } catch (IOException e) {
            logger.error("ERROR AL GUARDAR LA IMAGEN", e);
            return null;
        }

    }

}
