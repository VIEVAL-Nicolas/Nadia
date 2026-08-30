package com.story.nadia.model;

public record NextNode(Node node) implements StepResult {
    public NextNode {
        if (node == null) {
            throw new IllegalArgumentException("Le nœud suivant ne peut pas être nul.");
        }
    }

    public Node getNode() {
        return node;
    }
}