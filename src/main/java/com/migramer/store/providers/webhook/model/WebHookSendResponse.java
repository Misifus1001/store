package com.migramer.store.providers.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebHookSendResponse {
    private String message;
}