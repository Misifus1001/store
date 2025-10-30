package com.migramer.store.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate template;

    public void broadcastChange(Object data) {
        template.convertAndSend("/topic/changes", data);
    }
    
}