package com.migramer.store.providers;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.migramer.store.models.EmailRequest;

@Component
public class EmailProvider {

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.password}")
    private String password;

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

    public void sendEmail(EmailRequest emailRequest) {

        try {

            loadProperties();

            loadAutentication();

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

            logger.info("Mensaje envíado");

        } catch (Exception e) {
            logger.error("Ocurrió un error al mandar el mensaje", e);
        }

    }

}