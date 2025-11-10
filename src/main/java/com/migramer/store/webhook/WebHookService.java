package com.migramer.store.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.migramer.store.webhook.model.NameNotification;
import com.migramer.store.webhook.model.WebHookSendResponse;

@Service
public class WebHookService {

    @Autowired
    private SimpMessagingTemplate template;

    private final Logger logger = LoggerFactory.getLogger(WebHookService.class);

    public void sendNotificationChanges(NameNotification nameNotification, String uuidTienda) {

        String sendNotificationTo = nameNotification.getDescripcion();

        try {
            WebHookSendResponse webHookSendResponse = new WebHookSendResponse(buildMessage(sendNotificationTo, uuidTienda));
            template.convertAndSend(buildURL(sendNotificationTo, uuidTienda), webHookSendResponse);
        } catch (Exception e) {
            logger.error("ERROR notificando: " + sendNotificationTo, e);
        }
    }

    protected String buildURL(String endpoint, String uuidTienda) {
        return "/" + "webhook" + "/" + endpoint + "?uuidTienda=" + uuidTienda;
    }

    protected String buildMessage(String message, String uuidTienda) {
        return "CAMBIOS DETECTADOS EN:" + " " + message + " " + "PARA" + " " + "UUIDTIENDA:" + " " + uuidTienda;
    }

}