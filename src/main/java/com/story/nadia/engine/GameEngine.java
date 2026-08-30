package com.story.nadia.engine;

import java.util.List;
import java.util.Scanner;

import com.story.nadia.model.Choice;
import com.story.nadia.model.GameOver;
import com.story.nadia.model.Inventory;
import com.story.nadia.model.Item;
import com.story.nadia.model.NextNode;
import com.story.nadia.model.Node;
import com.story.nadia.model.StepResult;
import com.story.nadia.model.Victory;

public class GameEngine {
    private final Scanner scanner = new Scanner(System.in);
    private final Inventory inventory = new Inventory();

    public Inventory getInventory() {
        return inventory;
    }

    public void run(Node startNode) {
        Node currentNode = startNode;

        while (true) {
            System.out.println("\n" + currentNode.text());
            System.out.println(inventory.summary());
            List<Choice> choices = currentNode.choices();

            System.out.println("0 : Quitter");
            for (int i = 0; i < choices.size(); i++) {
                System.out.printf("%d : %s%n", i + 1, choices.get(i).text());
            }

            int selection = promptInt(0, choices.size());
            if (selection == 0) {
                System.out.println("\nMerci d'avoir joué. À bientôt !");
                return;
            }

            Choice chosen = choices.get(selection - 1);
            boolean takeItem = chosen.item() == null || promptTakeItem(chosen.item());
            StepResult result = resolveChoice(chosen, takeItem);

            switch (result) {
                case NextNode next -> currentNode = next.node();
                case GameOver fail -> {
                    System.out.println("\n--------------------------------------------------");
                    System.out.println(fail.reason());
                    System.out.println("Ceci est une création de Crazy Sunny.");
                    System.out.println("--------------------------------------------------");
                    return;
                }
                case Victory win -> {
                    System.out.println("\n" + win.message());
                    System.out.println("   0=====]::::::::FIN::::::::>");
                    System.out.println("C'est une création de Crazy Sunny !!!!");
                    return;
                }
            }
        }
    }

    public StepResult resolveChoice(Choice choice) {
        return resolveChoice(choice, true);
    }

    public StepResult resolveChoice(Choice choice, boolean takeItem) {
        if (choice.item() != null) {
            System.out.println("Vous avez trouvé : " + choice.item().name() + ".");
            if (choice.item().falseLead()) {
                System.out.println("Attention : cet objet semble être une fausse piste.");
            }

            if (inventory.isFull()) {
                System.out.println("Votre inventaire est plein (5 objets max). Vous ne pouvez pas prendre cet objet.");
                return choice.action().get();
            }

            if (!takeItem) {
                System.out.println("Vous laissez l'objet sur place.");
                return choice.action().get();
            }

            if (!inventory.canAdd(choice.item())) {
                System.out.println("Vous ne pouvez pas ajouter cet objet. L'inventaire est déjà plein ou l'objet est déjà présent.");
                return choice.action().get();
            }

            inventory.add(choice.item());
            System.out.println("Objet ajouté à l'inventaire : " + choice.item().name() + ".");
        }
        return choice.action().get();
    }

    private boolean promptTakeItem(com.story.nadia.model.Item item) {
        if (item == null) {
            return true;
        }

        System.out.println("Vous avez trouvé : " + item.name() + ".");
        if (item.falseLead()) {
            System.out.println("Attention : cet objet semble être une fausse piste.");
        }

        if (inventory.isFull()) {
            System.out.println("Votre inventaire est plein (5 objets max). Vous ne pouvez pas prendre cet objet.");
            return false;
        }

        System.out.print("Voulez-vous le prendre ? (1 = oui, 2 = non) : ");
        int answer = promptInt(1, 2);
        return answer == 1;
    }

    private int promptInt(int min, int max) {
        while (true) {
            System.out.print("\nVotre choix : ");
            if (scanner.hasNextInt()) {
                int val = scanner.nextInt();
                if (val >= min && val <= max) return val;
            } else {
                scanner.next();
            }
            System.out.printf("Veuillez choisir un nombre entre %d et %d.%n", min, max);
        }
    }
}
