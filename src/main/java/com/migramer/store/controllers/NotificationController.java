// package com.migramer.store.controllers;

// import java.util.Map;

// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.migramer.store.providers.PushNotificationService;

// @RestController
// @RequestMapping("/api/push")
// public class NotificationController {

//     private final PushNotificationService pushService;

//     public NotificationController(PushNotificationService pushService) {
//         this.pushService = pushService;
//     }

//     @PostMapping("/test")
//     public String test(@RequestBody Map<String, String> request) {
//         return pushService.sendToDevice(
//                 request.get("token"),
//                 request.get("title"),
//                 request.get("message"));
//     }

// }
