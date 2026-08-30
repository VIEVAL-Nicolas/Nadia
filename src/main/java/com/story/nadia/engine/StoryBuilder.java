package com.story.nadia.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.story.nadia.model.Item;
import com.story.nadia.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class StoryBuilder {
    private static final ObjectMapper JSON = new ObjectMapper();
    public static final StoryBuilder INSTANCE = new StoryBuilder();

    private StoryBuilder() {
    }

    public Node buildStory() {
        return buildStory("stories/nadia.json");
    }

    public Node buildStory(String resourcePath) {
        try (InputStream input = openResource(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException("Fichier de story introuvable : " + resourcePath);
            }

            JsonNode storyJson = JSON.readTree(input);
            JsonNode nodesJson = requireObject(storyJson.get("nodes"), "nodes");
            Map<String, Node> nodesById = new HashMap<>();

            Iterator<Map.Entry<String, JsonNode>> nodeEntries = nodesJson.fields();
            while (nodeEntries.hasNext()) {
                Map.Entry<String, JsonNode> entry = nodeEntries.next();
                String nodeId = entry.getKey();
                JsonNode nodeData = requireObject(entry.getValue(), "nœud " + nodeId);
                String text = requireText(nodeData, "text", "nœud " + nodeId);
                String imagePath = asText(nodeData.get("image"), "images/scene.png");
                nodesById.put(nodeId, new Node(text, imagePath));
            }

            nodeEntries = nodesJson.fields();
            while (nodeEntries.hasNext()) {
                Map.Entry<String, JsonNode> entry = nodeEntries.next();
                String nodeId = entry.getKey();
                Node currentNode = nodesById.get(nodeId);
                JsonNode nodeData = requireObject(entry.getValue(), "nœud " + nodeId);
                JsonNode choicesJson = nodeData.get("choices");
                if (choicesJson == null || choicesJson.isNull()) {
                    continue;
                }

                if (!choicesJson.isArray()) {
                    throw new IllegalArgumentException("Le champ 'choices' du nœud '" + nodeId + "' doit être un tableau JSON.");
                }

                for (JsonNode choiceValue : choicesJson) {
                    JsonNode choice = requireObject(choiceValue, "choix du nœud " + nodeId);
                    String choiceText = requireText(choice, "text", "choix du nœud " + nodeId);
                    String type = asText(choice.get("type"), "next");

                    switch (type) {
                        case "next" -> {
                            String targetId = requireText(choice, "target", "choix '" + choiceText + "'");
                            Node nextNode = nodesById.get(targetId);
                            if (nextNode == null) {
                                throw new IllegalArgumentException("Le nœud cible '" + targetId + "' est introuvable pour le choix '" + choiceText + "'.");
                            }
                            Item item = readItem(choice);
                            if (item != null) {
                                currentNode.choices().add(new com.story.nadia.model.Choice(choiceText, () -> new com.story.nadia.model.NextNode(nextNode), com.story.nadia.model.Outcome.NEXT, item));
                            } else {
                                currentNode.choices().add(new com.story.nadia.model.Choice(choiceText, () -> new com.story.nadia.model.NextNode(nextNode), com.story.nadia.model.Outcome.NEXT, null));
                            }
                        }
                        case "fail" -> {
                            String failureMessage = requireText(choice, "message", "choix '" + choiceText + "'");
                            currentNode.choices().add(new com.story.nadia.model.Choice(choiceText, () -> new com.story.nadia.model.GameOver(failureMessage), com.story.nadia.model.Outcome.FAIL, readItem(choice)));
                        }
                        case "victory" -> {
                            String victoryMessage = requireText(choice, "message", "choix '" + choiceText + "'");
                            currentNode.choices().add(new com.story.nadia.model.Choice(choiceText, () -> new com.story.nadia.model.Victory(victoryMessage), com.story.nadia.model.Outcome.VICTORY, readItem(choice)));
                        }
                        default -> throw new IllegalArgumentException("Type de choix inconnu '" + type + "' pour '" + choiceText + "'.");
                    }
                }
            }

            String startNodeId = requireText(storyJson, "startNode", "fichier histoire");
            Node startNode = nodesById.get(startNodeId);
            if (startNode == null) {
                throw new IllegalArgumentException("Le nœud de départ '" + startNodeId + "' est introuvable.");
            }
            return startNode;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger l'histoire depuis '" + resourcePath + "'.", e);
        }
    }

    private InputStream openResource(String resourcePath) throws IOException {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (resourceStream != null) {
            return resourceStream;
        }

        Path filePath = Paths.get(resourcePath);
        if (Files.exists(filePath)) {
            return Files.newInputStream(filePath);
        }

        return null;
    }

    private static Item readItem(JsonNode choice) {
        JsonNode itemNode = choice.get("item");
        if (itemNode == null || itemNode.isNull()) {
            return null;
        }
        String itemName = asText(itemNode, null);
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Le champ 'item' doit contenir un nom valide.");
        }
        return com.story.nadia.model.ItemCatalog.INSTANCE.require(itemName);
    }

    private static JsonNode requireObject(JsonNode value, String context) {
        if (value == null || value.isNull() || !value.isObject()) {
            throw new IllegalArgumentException("La valeur attendue pour '" + context + "' doit être un objet JSON.");
        }
        return value;
    }

    private static String requireText(JsonNode node, String key, String context) {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est absent pour '" + context + "'.");
        }
        String text = asText(value, null);
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' doit être non vide pour '" + context + "'.");
        }
        return text;
    }

    private static String asText(JsonNode value, String defaultValue) {
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        if (value.isTextual() || value.isNumber() || value.isBoolean()) {
            return value.asText();
        }
        return value.toString();
    }
}
