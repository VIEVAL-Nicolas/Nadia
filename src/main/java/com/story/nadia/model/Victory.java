package com.story.nadia.model;

public record Victory(String message) implements StepResult {
    public Victory {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Le message de victoire ne peut pas être vide.");
        }
    }

    public String getMessage() {
        return message;
    }
}