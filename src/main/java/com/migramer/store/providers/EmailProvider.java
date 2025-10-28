package com.migramer.store.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.migramer.store.models.EmailRequest;
import com.migramer.store.models.EmailResponse;

@Component
public class EmailProvider {

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.password}")
    private String password;

    private final String fileName = "logo_loopers_name.jpeg";

    private Authenticator authenticator;

    private Properties properties;

    private final Logger logger = LoggerFactory.getLogger(EmailProvider.class);

    protected void loadProperties() {
        properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.user", emailFrom);
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.auth", "true");
    }

    protected void loadAutentication() {
        authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailFrom, password);
            }
        };

    }

    public EmailResponse sendEmail(EmailRequest emailRequest) {

        try {

            loadProperties();

            loadAutentication();

            EmailResponse emailResponse = new EmailResponse();

            Session session = Session.getInstance(properties, authenticator);

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailFrom));
            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");

            InternetAddress[] toAddresses = { new InternetAddress(emailRequest.getEmailTo()) };
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            message.setSubject(emailRequest.getSubject());
            // MimeBodyPart mimeBodyPart = new MimeBodyPart();
            // mimeBodyPart.setContent(emailRequest.getMessage(), "text/html");

            // Multipart multipart = new MimeMultipart(mimeBodyPart);
            // message.setContent(multipart);

            message.setText(emailRequest.getMessage());
            message.setSentDate(new Date());
            Transport.send(message);

            String operacionExitosa = "Mensaje envíado";
            logger.info(operacionExitosa);
            emailResponse.setMensaje(operacionExitosa);

            // sendImageEmail(emailRequest.getEmailTo(), emailRequest.getSubject(),
            emailRequest.getMessage();

            return emailResponse;

        } catch (Exception e) {
            logger.error("Ocurrió un error al mandar el mensaje", e);
            throw new RuntimeException(e);
        }

    }

    private ByteArrayDataSource getLogoImage() {

        try {

            String filename = "logo_loopers_name.jpeg";
            String resourcePath = "/static/img/" + filename;

            InputStream imageStream = getClass().getResourceAsStream(resourcePath);

            if (imageStream == null) {
                throw new RuntimeException("Image file not found in classpath: " + resourcePath);
            }

            ByteArrayDataSource ds = new ByteArrayDataSource(imageStream, "image/jpeg");

            return ds;
        } catch (IOException e) {
            logger.error("ERROR: ", e);
            throw new RuntimeException(e);
        }
    }

    private String getHtmlContent(String message) {

        String htmlContent = "<h4>" + message + "</h4>"
                + "<img src='cid:logoImage' style='width:100px;heigth:100px;'>";
        return htmlContent;
    }

    public EmailResponse sendImageEmail(EmailRequest emailRequest) {
        try {
            loadProperties();
            loadAutentication();

            EmailResponse emailResponse = new EmailResponse();

            Session session = Session.getInstance(properties, authenticator);

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8");
            mimeMessage.addHeader("format", "flowed");
            mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");

            mimeMessage.setFrom(new InternetAddress(emailFrom, "NoReply"));
            mimeMessage.setReplyTo(InternetAddress.parse(emailFrom, false));
            mimeMessage.setSubject(emailRequest.getSubject(), "UTF-8");
            mimeMessage.setSentDate(new Date());
            mimeMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailRequest.getEmailTo(), false));

            MimeMultipart multipart = new MimeMultipart("related");

            MimeBodyPart htmlBodyPart = new MimeBodyPart();

            htmlBodyPart.setContent(getHtmlContent(emailRequest.getMessage()), "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlBodyPart);

            MimeBodyPart imageBodyPart = new MimeBodyPart();

            imageBodyPart.setDataHandler(new DataHandler(getLogoImage()));

            imageBodyPart.setFileName(fileName);

            imageBodyPart.setHeader("Content-ID", "<logoImage>");
            imageBodyPart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imageBodyPart);

            mimeMessage.setContent(multipart);

            Transport.send(mimeMessage);
            emailResponse.setMensaje("Mensaje envíado");

            return emailResponse;
        } catch (MessagingException e) {
            logger.error("ERROR: ", e);
            throw new RuntimeException(e);

        } catch (UnsupportedEncodingException e) {
            logger.error("ERROR: ", e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            logger.error("ERROR: ", e);
            throw new RuntimeException(e);
        }
    }

}