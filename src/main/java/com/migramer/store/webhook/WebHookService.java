package com.migramer.store.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebHookService {

    @Autowired
    private SimpMessagingTemplate template;

    private final Logger logger = LoggerFactory.getLogger(WebHookService.class);

    public void sendNotificationChanges(String endpoint, String uuidTienda) {
        try {
            template.convertAndSend(buildURL(endpoint, uuidTienda),"USUARIO ACTUALIZADO");
        } catch (Exception e) {
            logger.error("ERROR notificando: " + endpoint, e);
        }
    }

    protected String buildURL(String endpoint, String uuidTienda) {
        return "/" + "webhook" + "/" + endpoint + "?uuidTienda=" + uuidTienda;
    }

}