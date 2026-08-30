package com.story.nadia.model;

public record Item(String name, boolean falseLead, String imagePath) {
    public Item {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom d'un item ne peut pas être vide.");
        }
    }

    public Item(String name) {
        this(name, false, "images/" + name.trim().toLowerCase().replaceAll("\\s+", "-") + ".png");
    }

    public Item(String name, boolean falseLead) {
        this(name, falseLead, "images/" + name.trim().toLowerCase().replaceAll("\\s+", "-") + ".png");
    }
}
