package com.migramer.store.firebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.firebase.model.PushNotificationRequest;
import com.migramer.store.firebase.model.PushNotificationResponse;
import com.migramer.store.firebase.service.PushNotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/firebase")
public class FirebaseController {

    @Autowired
    private PushNotificationService pushNotificationService;

    @PostMapping("/push-notification")
    public PushNotificationResponse pushNotification(@Valid @RequestBody PushNotificationRequest pushNotificationRequest) {
        return pushNotificationService.sendToDevice(pushNotificationRequest);
    }

}
