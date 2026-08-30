package com.story.nadia.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class ItemCatalog {
    private static final ObjectMapper JSON = new ObjectMapper();
    public static final ItemCatalog INSTANCE = new ItemCatalog();

    private final Map<String, Item> itemsByName = new HashMap<>();

    private ItemCatalog() {
        loadItems("items/nadia-items.json");
    }

    public Item get(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return itemsByName.get(name.trim());
    }

    public Item require(String name) {
        Item item = get(name);
        if (item == null) {
            throw new IllegalArgumentException("L'item '" + name + "' est introuvable dans le catalogue.");
        }
        return item;
    }

    private void loadItems(String resourcePath) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Catalogue d'items introuvable : " + resourcePath);
            }

            JsonNode root = JSON.readTree(input);
            JsonNode itemsNode = root.get("items");
            if (itemsNode == null || !itemsNode.isArray()) {
                throw new IllegalArgumentException("Le catalogue d'items doit contenir un tableau 'items'.");
            }

            for (JsonNode itemNode : itemsNode) {
                Item item = readItem(itemNode);
                itemsByName.put(item.name(), item);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger le catalogue d'items depuis '" + resourcePath + "'.", e);
        }
    }

    private Item readItem(JsonNode itemNode) {
        if (itemNode == null || itemNode.isNull()) {
            throw new IllegalArgumentException("Un item du catalogue ne peut pas être vide.");
        }
        if (itemNode.isTextual()) {
            String name = itemNode.asText();
            return new Item(name);
        }
        if (itemNode.isObject()) {
            JsonNode nameNode = itemNode.get("name");
            if (nameNode == null || !nameNode.isTextual()) {
                throw new IllegalArgumentException("Un item objet doit contenir un champ 'name' texte.");
            }
            String name = nameNode.asText();
            JsonNode imageNode = itemNode.get("image");
            String imagePath = imageNode != null && imageNode.isTextual() ? imageNode.asText() : "images/" + name.toLowerCase() + ".png";
            return new Item(name, false, imagePath);
        }
        throw new IllegalArgumentException("Format d'item invalide dans le catalogue : " + itemNode);
    }

    public Item resolve(String itemName) {
        return get(itemName);
    }
}

