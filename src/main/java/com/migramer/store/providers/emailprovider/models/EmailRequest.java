package com.migramer.store.providers.emailprovider.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailRequest {

    @NotBlank(message = "El campo 'subject' es **obligatorio**.")
    @Size(max = 255, message = "El asunto ('subject') no debe exceder los **255 caracteres**.")
    private String subject;

    @NotBlank(message = "El campo 'emailTo' es **obligatorio**.")
    @Email(message = "El valor de 'emailTo' debe ser una **dirección de correo electrónico válida**.")
    private String emailTo;

    @NotBlank(message = "El campo 'message' es **obligatorio**.")
    @Size(max = 1000, message = "El mensaje ('message') no debe exceder los **1000 caracteres**.")
    private String message;
}
