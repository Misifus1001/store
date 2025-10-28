package com.migramer.store.models;

import lombok.Data;

@Data
public class EmailRequest {
    private String subject;
    private String emailTo;
    private String message;
}