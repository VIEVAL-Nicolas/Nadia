package com.story.nadia.model;

public record GameOver(String reason) implements StepResult {
    public GameOver {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Le message d'échec ne peut pas être vide.");
        }
    }

    public String getReason() {
        return reason;
    }
}