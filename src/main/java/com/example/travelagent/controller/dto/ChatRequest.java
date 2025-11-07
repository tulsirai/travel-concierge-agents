package com.example.travelagent.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    /**
     * Optional identifier that lets clients keep multiple conversations.
     */
    private String conversationId;

    @NotBlank
    private String message;
}
