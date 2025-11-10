// package com.migramer.store.providers;

// import org.springframework.stereotype.Service;

// import com.google.firebase.messaging.AndroidConfig;
// import com.google.firebase.messaging.FirebaseMessaging;
// import com.google.firebase.messaging.Message;

// @Service
// public class PushNotificationService {

//     public String sendToDevice(String token, String title, String body) {
        
//         // Message message = Message.builder()
//         //         .setToken(token)
//         //         .putData("title", title)
//         //         .putData("body", body)
//         //         .build();

//         Message message = Message.builder()
//             .setToken(token)
//             .putData("title", title)
//             .putData("body", body)
//             .setAndroidConfig(AndroidConfig.builder()
//                 .setPriority(AndroidConfig.Priority.HIGH)
//                 .build())
//             .build();

//         try {
//             return FirebaseMessaging.getInstance().send(message);
//         } catch (Exception e) {
//             throw new RuntimeException("Error enviando notificación: " + e.getMessage());
//         }
//     }
    
// }
