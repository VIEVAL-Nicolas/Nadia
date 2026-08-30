package com.story.nadia.model;

import java.util.Objects;
import java.util.function.Supplier;

public record Choice(String text, Supplier<StepResult> action, Outcome outcome, Item item) {
    public Choice {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Le texte d'un choix ne peut pas être vide.");
        }
        action = Objects.requireNonNull(action, "Une action est requise pour un choix.");
        outcome = Objects.requireNonNull(outcome, "Le type d'issue du choix est requis.");
    }

    public static Choice next(String text, Node target) {
        return new Choice(text, () -> new NextNode(target), Outcome.NEXT, null);
    }

    public static Choice fail(String text, String failureMessage) {
        return new Choice(text, () -> new GameOver(failureMessage), Outcome.FAIL, null);
    }

    public static Choice victory(String text, String victoryMessage) {
        return new Choice(text, () -> new Victory(victoryMessage), Outcome.VICTORY, null);
    }

    public static Choice custom(String text, Supplier<StepResult> action) {
        return new Choice(text, action, Outcome.NEXT, null);
    }

    public static Choice withItem(String text, Node target, Item item) {
        return new Choice(text, () -> new NextNode(target), Outcome.NEXT, item);
    }

    public static Choice withItem(String text, Supplier<StepResult> action, Item item) {
        return new Choice(text, action, Outcome.NEXT, item);
    }

    public String getText() {
        return text;
    }

    public Supplier<StepResult> getAction() {
        return action;
    }

    public Outcome getOutcome() {
        return outcome;
    }
}