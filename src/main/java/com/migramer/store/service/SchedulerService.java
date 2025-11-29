package com.migramer.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.migramer.store.providers.firebase.service.PushNotificationService;

@Component
public class SchedulerService {

    @Autowired
    private PushNotificationService pushNotificationService;

    // @Scheduled(cron = "0 45 8 1/15 * ?")
    @Scheduled(cron = "0 05 18 * * *")
    public void pushNotification(){
        pushNotificationService.enviarPushNotifications();
    }
    
}