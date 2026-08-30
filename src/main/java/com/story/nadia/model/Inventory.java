package com.story.nadia.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Inventory {
    public static final int MAX_ITEMS = 5;

    private final Map<String, Item> items = new LinkedHashMap<>();

    public void add(Item item) {
        if (item == null || !canAdd(item)) {
            return;
        }
        items.putIfAbsent(item.name(), item);
    }

    public boolean canAdd(Item item) {
        return item != null && !isFull() && !items.containsKey(item.name());
    }

    public boolean isFull() {
        return items.size() >= MAX_ITEMS;
    }

    public boolean containsItem(String itemName) {
        return itemName != null && items.containsKey(itemName);
    }

    public boolean containsItem(Item item) {
        return item != null && items.containsKey(item.name());
    }

    public int size() {
        return items.size();
    }

    public List<Item> items() {
        return List.copyOf(items.values());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public String summary() {
        if (items.isEmpty()) {
            return "Inventaire : vide";
        }
        return "Inventaire : " + items.values().stream()
                .map(Item::name)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
