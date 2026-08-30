package com.story.nadia.ui;

import com.story.nadia.engine.GameEngine;
import com.story.nadia.engine.StoryBuilder;
import com.story.nadia.model.Choice;
import com.story.nadia.model.GameOver;
import com.story.nadia.model.Inventory;
import com.story.nadia.model.Item;
import com.story.nadia.model.NextNode;
import com.story.nadia.model.Node;
import com.story.nadia.model.StepResult;
import com.story.nadia.model.Victory;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class JavaFxApp extends Application {
    private final GameEngine gameEngine = new GameEngine();
    private Node currentNode;
    private final GridPane choicesGrid = new GridPane();
    private final Label textLabel = new Label();
    private final Label inventoryLabel = new Label();
    private final HBox inventorySlots = new HBox(8);
    private final VBox inventoryBar = new VBox(6);
    private final ImageView imageView = new ImageView();

    @Override
    public void start(Stage stage) {
        currentNode = StoryBuilder.INSTANCE.buildStory();

        stage.setTitle("NADIA");

        textLabel.setWrapText(true);
        textLabel.setMaxWidth(Double.MAX_VALUE);
        textLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #f5f5f5;");

        imageView.setFitWidth(620);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        choicesGrid.setHgap(12);
        choicesGrid.setVgap(8);
        choicesGrid.setAlignment(Pos.CENTER_LEFT);
        choicesGrid.setPadding(new Insets(8, 0, 0, 0));

        inventoryLabel.setText("Inventaire");
        inventoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold;");
        inventoryBar.setAlignment(Pos.CENTER_LEFT);
        inventoryBar.setPadding(new Insets(10, 12, 10, 12));
        inventoryBar.setStyle("-fx-border-color: white; -fx-border-width: 1 0 0 0; -fx-background-color: #1d1d1d;");
        inventorySlots.setAlignment(Pos.CENTER_LEFT);
        inventorySlots.setSpacing(12);
        inventorySlots.setFillHeight(true);
        inventoryBar.getChildren().addAll(inventoryLabel, inventorySlots);
        HBox.setHgrow(inventorySlots, Priority.ALWAYS);

        VBox root = new VBox(12);
        root.setPadding(new Insets(18, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #1d1d1d;");
        root.getChildren().addAll(imageView, textLabel, choicesGrid, inventoryBar);
        VBox.setVgrow(textLabel, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(choicesGrid, javafx.scene.layout.Priority.ALWAYS);

        root.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #1d1d1d; -fx-background-color: #1d1d1d;");

        Scene scene = new Scene(scrollPane, 700, 760, Color.web("#1d1d1d"));
        scene.widthProperty().addListener((obs, oldValue, newValue) -> renderChoiceButtons());
        stage.setScene(scene);
        renderCurrentNode();
        stage.show();
    }

    private void renderCurrentNode() {
        textLabel.setText(currentNode.text());
        renderInventory();
        imageView.setImage(loadImageForNode(currentNode));
        renderChoiceButtons();
    }

    private void renderInventory() {
        inventoryLabel.setText("Inventaire (" + gameEngine.getInventory().size() + "/" + Inventory.MAX_ITEMS + ")");
        inventorySlots.getChildren().clear();

        for (int i = 0; i < Inventory.MAX_ITEMS; i++) {
            VBox slot = new VBox(4);
            slot.setAlignment(Pos.CENTER);
            slot.setPrefSize(90, 104);
            slot.setMinSize(90, 104);
            slot.setMaxSize(90, 104);

            if (i < gameEngine.getInventory().size()) {
                Item item = gameEngine.getInventory().items().get(i);
                StackPane pictureArea = createItemPictureSlot(item.name());
                Label itemName = new Label(item.name());
                itemName.setWrapText(true);
                itemName.setMaxWidth(78);
                itemName.setAlignment(Pos.CENTER);
                itemName.setStyle("-fx-font-size: 10px; -fx-text-fill: white; -fx-font-weight: bold;");
                slot.getChildren().addAll(pictureArea, itemName);
            } else {
                Rectangle emptySquare = new Rectangle(64, 64);
                emptySquare.setArcWidth(12);
                emptySquare.setArcHeight(12);
                emptySquare.setFill(Color.web("#1d1d1d"));
                emptySquare.setStroke(Color.web("#dfeeff"));
                emptySquare.setStrokeWidth(1.5);
                slot.getChildren().add(emptySquare);
            }

            inventorySlots.getChildren().add(slot);
        }
    }

    private StackPane createItemPictureSlot(String itemName) {
        StackPane pictureArea = new StackPane();
        Rectangle square = new Rectangle(64, 64);
        square.setArcWidth(12);
        square.setArcHeight(12);
        square.setFill(Color.web("#2e5da8"));
        square.setStroke(Color.web("#dfeeff"));
        square.setStrokeWidth(1.5);

        var item = gameEngine.getInventory().items().stream()
                .filter(current -> current.name().equals(itemName))
                .findFirst()
                .orElse(new Item(itemName));

        var stream = getClass().getClassLoader().getResourceAsStream(item.imagePath());
        if (stream != null) {
            Image itemImage = new Image(stream);
            ImageView itemView = new ImageView(itemImage);
            itemView.setFitWidth(52);
            itemView.setFitHeight(52);
            itemView.setPreserveRatio(true);
            pictureArea.getChildren().addAll(square, itemView);
            return pictureArea;
        }

        Label icon = new Label("IMG");
        icon.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #eaf3ff;");

        Label shortName = new Label(itemName.substring(0, Math.min(3, itemName.length())).toUpperCase());
        shortName.setStyle("-fx-font-size: 10px; -fx-text-fill: white; -fx-font-weight: bold;");
        shortName.setTranslateY(16);

        pictureArea.getChildren().addAll(square, icon, shortName);
        return pictureArea;
    }

    private void renderChoiceButtons() {
        choicesGrid.getChildren().clear();
        double sceneWidth = textLabel.getScene() != null ? textLabel.getScene().getWidth() : 700;
        int columns = sceneWidth >= 900 ? 2 : 1;

        for (int i = 0; i < currentNode.choices().size(); i++) {
            Choice choice = currentNode.choices().get(i);
            Button button = createChoiceButton(choice);
            int row = i / columns;
            int col = i % columns;
            choicesGrid.add(button, col, row);
        }

        choicesGrid.getColumnConstraints().clear();
        for (int i = 0; i < columns; i++) {
            javafx.scene.layout.ColumnConstraints constraint = new javafx.scene.layout.ColumnConstraints();
            constraint.setPercentWidth(100.0 / columns);
            constraint.setFillWidth(true);
            choicesGrid.getColumnConstraints().add(constraint);
        }
    }

    private Button createChoiceButton(Choice choice) {
        Button button = new Button(choice.text());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setMinHeight(26);
        button.setWrapText(true);
        button.setStyle("-fx-font-size: 12px; -fx-padding: 6 8; -fx-background-color: #2f5d9f; -fx-text-fill: white;");
        button.setOnAction(event -> resolveChoice(choice));
        return button;
    }

    private void resolveChoice(Choice choice) {
        StepResult result = gameEngine.resolveChoice(choice);

        if (result instanceof NextNode nextNode) {
            currentNode = nextNode.node();
            renderCurrentNode();
            return;
        }

        if (result instanceof GameOver gameOver) {
            textLabel.setText(gameOver.reason());
            renderInventory();
            choicesGrid.getChildren().clear();
            Button restartButton = new Button("Rejouer");
            restartButton.setMaxWidth(Double.MAX_VALUE);
            restartButton.setMinHeight(34);
            restartButton.setStyle("-fx-font-size: 13px; -fx-padding: 8 10; -fx-background-color: #2f5d9f; -fx-text-fill: white;");
            restartButton.setOnAction(event -> {
                currentNode = StoryBuilder.INSTANCE.buildStory();
                gameEngine.getInventory().clear();
                renderCurrentNode();
            });
            choicesGrid.add(restartButton, 0, 0);
            return;
        }

        if (result instanceof Victory victory) {
            textLabel.setText(victory.message());
            renderInventory();
            choicesGrid.getChildren().clear();
            Button restartButton = new Button("Recommencer");
            restartButton.setMaxWidth(Double.MAX_VALUE);
            restartButton.setMinHeight(34);
            restartButton.setStyle("-fx-font-size: 13px; -fx-padding: 8 10; -fx-background-color: #2f5d9f; -fx-text-fill: white;");
            restartButton.setOnAction(event -> {
                currentNode = StoryBuilder.INSTANCE.buildStory();
                gameEngine.getInventory().clear();
                renderCurrentNode();
            });
            choicesGrid.add(restartButton, 0, 0);
        }
    }

    private Image loadImageForNode(Node node) {
        String imageName = node.imagePath() != null && !node.imagePath().isBlank() ? node.imagePath() : switch (node.text().toLowerCase()) {
            case String s when s.contains("nadia") -> "images/nadia.png";
            case String s when s.contains("mauvais") || s.contains("perdu") -> "images/gameover.png";
            case String s when s.contains("victoire") || s.contains("fin") -> "images/victory.png";
            default -> "images/scene.png";
        };

        var stream = getClass().getClassLoader().getResourceAsStream(imageName);
        if (stream != null) {
            return new Image(stream);
        }

        var fallback = getClass().getClassLoader().getResourceAsStream("images/scene.png");
        if (fallback != null) {
            return new Image(fallback);
        }

        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAF" +
                "kR0YAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJ0UkGAAAAA" +
                "AQMEBQAAAAAABJRU5ErkJggg==");
    }
}
