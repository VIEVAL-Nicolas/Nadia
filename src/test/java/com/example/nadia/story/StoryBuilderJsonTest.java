package com.example.nadia.story;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.story.nadia.engine.GameEngine;
import com.story.nadia.engine.StoryBuilder;
import com.story.nadia.model.Inventory;
import com.story.nadia.model.Item;
import com.story.nadia.model.Node;

class StoryBuilderJsonTest {

    @Test
    void shouldBuildStoryFromJsonResource() {
        Node root = StoryBuilder.INSTANCE.buildStory("stories/nadia.json");

        assertNotNull(root);
        assertFalse(root.choices().isEmpty());
        assertTrue(root.text().contains("NADIA"));
    }

    @Test
    void shouldAddItemWhenChoiceProvidesOne() {
        GameEngine gameEngine = new GameEngine();
        Node root = StoryBuilder.INSTANCE.buildStory("stories/nadia.json");

        var itemChoice = root.choices().stream()
                .filter(choice -> choice.item() != null)
                .findFirst()
                .orElse(root.choices().getFirst());

        gameEngine.resolveChoice(itemChoice);

        assertTrue(gameEngine.getInventory().containsItem("amulette"));
    }

    @Test
    void shouldNotAllowMoreThanFiveItems() {
        Inventory inventory = new Inventory();

        for (int i = 0; i < Inventory.MAX_ITEMS; i++) {
            inventory.add(new Item("objet-" + i));
        }

        assertTrue(inventory.isFull());
        assertFalse(inventory.canAdd(new Item("objet-6")));
    }

    @Test
    void shouldLoadItemImageMetadataFromCatalog() {
        Item amulette = com.story.nadia.model.ItemCatalog.INSTANCE.require("amulette");

        assertNotNull(amulette);
        assertTrue(amulette.imagePath().endsWith("amulette.png"));
    }
}
