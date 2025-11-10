package com.migramer.store.firebase.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class PushNotificationRequest {

    @NotBlank(message = "El token de la notificación no puede estar vacío.")
    private String token;

    @NotBlank(message = "El título de la notificación no puede estar vacío.")
    @Size(max = 50, message = "El título no puede exceder los {max} caracteres.")
    private String title;

    @NotBlank(message = "El cuerpo de la notificación no puede estar vacío.")
    @Size(max = 256, message = "El cuerpo no puede exceder los {max} caracteres.")
    private String body;

}