package com.migramer.store.providers.emailprovider.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.migramer.store.providers.emailprovider.html.HtmlBody;
import com.migramer.store.providers.emailprovider.models.EmailRequest;
import com.migramer.store.providers.emailprovider.models.EmailResponse;
import com.migramer.store.providers.emailprovider.models.TypeHtmlBody;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailProvider {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private HtmlBody htmlBody;

    public EmailResponse sendEmail(EmailRequest emailRequest, TypeHtmlBody typeHtmlBody) {
        // executeSendSimpleEmail(
        // emailRequest.getEmailTo(),
        // emailRequest.getSubject(),
        // emailRequest.getMessage(),
        // typeHtmlBody);
        llamarProveedorEmail(emailRequest);
        return new EmailResponse("Mensaje enviado correctamente");
    }

    public void llamarProveedorEmail(EmailRequest emailRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EmailRequest> requestEntity = new HttpEntity<>(emailRequest, headers);

        Integer contador = 3;

        ResponseEntity<Object> response = null;

        do {

            try {

                if (response != null) {
                    break;
                }

                Thread.sleep(1000);

                response = new RestTemplate().exchange(
                        "https://unmistrustful-ununited-viva.ngrok-free.dev/providers/send-email",
                        HttpMethod.POST,
                        requestEntity,
                        Object.class);
            } catch (Exception e) {
                log.error("ERROR: ", e);
                contador++;
                response = null;
            }

        } while (contador >= 0);

    }

    public void executeSendSimpleEmail(String to, String subject, String text, TypeHtmlBody typeHtmlBody) {

        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

        // ResponseEntity<Object> response = new RestTemplate().exchange(
        //         "https://unmistrustful-ununited-viva.ngrok-free.dev",
        //         HttpMethod.POST,
        //         requestEntity,
        //         Object.class);

        // try {
        // MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // helper.setTo(to);
        // helper.setSubject(subject);

        // String htmlContent = htmlBody.getHTMLBody(text, typeHtmlBody);
        // helper.setText(htmlContent, true);

        // helper.addInline("logoImage", getLogoImage("logo_loopers_name.jpeg"));

        // javaMailSender.send(mimeMessage);

        // log.info("Correo enviado correctamente a {}", to);
        // } catch (MessagingException e) {
        // log.error("Error al enviar correo: ", e);
        // throw new RuntimeException("Error al enviar correo", e);
        // }

    }

    protected ByteArrayDataSource getLogoImage(String fileName) {
        try {
            String resourcePath = "/static/img/" + fileName;
            InputStream imageStream = getClass().getResourceAsStream(resourcePath);

            if (imageStream == null) {
                throw new RuntimeException("Imagen no encontrada en classpath: " + resourcePath);
            }

            String mimeType = fileName.endsWith(".png") ? "image/png" : "image/jpeg";

            return new ByteArrayDataSource(imageStream, mimeType);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando la imagen del logo", e);
        }
    }

}
