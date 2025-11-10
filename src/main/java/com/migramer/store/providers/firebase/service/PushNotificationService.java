package com.migramer.store.providers.firebase.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.migramer.store.providers.firebase.model.PushNotificationRequest;
import com.migramer.store.providers.firebase.model.PushNotificationResponse;

@Service
public class PushNotificationService {

    public PushNotificationResponse sendToDevice(PushNotificationRequest pushNotificationRequest) {

        PushNotificationResponse pushNotificationResponse = new PushNotificationResponse();

        Message message = Message.builder()
                .setToken(pushNotificationRequest.getToken())
                .putData("title", pushNotificationRequest.getTitle())
                .putData("body", pushNotificationRequest.getBody())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .build();

        try {
            String firebaseMessage = FirebaseMessaging.getInstance().send(message);
            pushNotificationResponse.setMessage(firebaseMessage);
            return pushNotificationResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error enviando notificación: " + e.getMessage());
        }
    }
}
