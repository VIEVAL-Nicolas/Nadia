package com.story.nadia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Node {
    private final String text;
    private final String imagePath;
    private final List<Choice> choices = new ArrayList<>();

    public Node(String text) {
        this(text, "images/scene.png");
    }

    public Node(String text, String imagePath) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Le texte d'un nœud ne peut pas être vide.");
        }
        this.text = text;
        this.imagePath = (imagePath == null || imagePath.isBlank()) ? "images/scene.png" : imagePath;
    }

    public Node addChoice(String text, Node nextNode) {
        choices.add(Choice.next(text, nextNode));
        return this;
    }

    public Node addFailingChoice(String text, String failureMessage) {
        choices.add(Choice.fail(text, failureMessage));
        return this;
    }

    public Node addCustomChoice(String text, Supplier<StepResult> action) {
        choices.add(Choice.custom(text, action));
        return this;
    }

    public String text() { return text; }
    public String imagePath() { return imagePath; }
    public List<Choice> choices() { return choices; }
}
