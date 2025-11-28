package com.migramer.store.providers.firebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migramer.store.models.MessageResponse;
import com.migramer.store.providers.firebase.model.FirebaseTokenDto;
// import com.migramer.store.providers.firebase.model.PushNotificationRequest;
// import com.migramer.store.providers.firebase.model.PushNotificationResponse;
// import com.migramer.store.providers.firebase.service.PushNotificationService;
import com.migramer.store.security.CustomUserDetails;
import com.migramer.store.service.FirebaseEntityTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/firebase")
public class FirebaseController {

    // @Autowired
    // private PushNotificationService pushNotificationService;

    @Autowired
    private FirebaseEntityTokenService firebaseEntityTokenService;

    // @PostMapping("/push-notification")
    // public PushNotificationResponse pushNotification(
    //         @Valid @RequestBody PushNotificationRequest pushNotificationRequest) {
    //     return pushNotificationService.sendToDevice(pushNotificationRequest);
    // }

    @PostMapping("/save-token")
    public ResponseEntity<MessageResponse> saveToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody FirebaseTokenDto firebaseTokenDto) {
        MessageResponse messageResponse = firebaseEntityTokenService.guardarToken(firebaseTokenDto.getToken(), userDetails.getUsername());
        return ResponseEntity.ok(messageResponse);
    }

}
