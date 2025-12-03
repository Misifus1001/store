package com.migramer.store.providers.firebase.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.migramer.store.entities.FirebaseEntityToken;
import com.migramer.store.providers.firebase.model.PushNotificationRequest;
import com.migramer.store.providers.firebase.model.PushNotificationResponse;
import com.migramer.store.service.FirebaseEntityTokenService;

@Service
public class PushNotificationService {

    @Autowired
    private FirebaseEntityTokenService firebaseEntityTokenService;

    private final Boolean envioActivado = true;
    private final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

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
            logger.error("ERROR", e);
            throw new RuntimeException("Error enviando notificación: " + e.getMessage());
        }
    }

    public void enviarPushNotifications() {

        if (envioActivado) {

            List<FirebaseEntityToken> firebaseEntityTokenList = firebaseEntityTokenService.getFirebaseEntityTokenList();

            List<PushNotificationRequest> pushNotificationRequestList = getPushNotificationRequests(
                    firebaseEntityTokenList);

            for (PushNotificationRequest pushNotificationRequest : pushNotificationRequestList) {
                sendToDevice(pushNotificationRequest);
            }
        }else{
            logger.info("ENVÍO DE NOTIFICACIONES DESACTIVADO");
        }

    }

    private List<PushNotificationRequest> getPushNotificationRequests(
            List<FirebaseEntityToken> firebaseEntityTokenList) {

        List<PushNotificationRequest> pushNotificationRequestList = new ArrayList<>();

        String title = "Hola desde spring boot";
        String body = "Esta es una prueba de push notifications";

        for (FirebaseEntityToken firebaseEntityToken : firebaseEntityTokenList) {

            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
            pushNotificationRequest.setTitle(title);
            pushNotificationRequest.setToken(firebaseEntityToken.getToken());
            pushNotificationRequest.setBody(body);

            pushNotificationRequestList.add(pushNotificationRequest);
        }

        return pushNotificationRequestList;

    }

}
