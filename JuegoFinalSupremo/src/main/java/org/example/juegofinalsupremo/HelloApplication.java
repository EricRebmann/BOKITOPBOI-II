package org.example.juegofinalsupremo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.juegofinalsupremo.data.MyList;
import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.io.GameJsonRepository;
import org.example.juegofinalsupremo.model.Cell;
import org.example.juegofinalsupremo.model.Direction;
import org.example.juegofinalsupremo.model.GameEngine;
import org.example.juegofinalsupremo.model.GameObject;
import org.example.juegofinalsupremo.model.GameState;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;

import java.io.IOException;

public class HelloApplication extends Application {
    private GameEngine engine;
    private GridPane mapGrid;
    private Label statusLabel;
    private ListView<String> inventoryList;
    private TextArea logArea;
    private Direction selectedDirection = Direction.UP;

    @Override
    public void start(Stage stage) throws IOException {
        engine = GameEngine.sampleGame();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setCenter(buildMap());
        root.setRight(buildSidePanel());
        root.setBottom(buildLogPanel());
        refresh();

        Scene scene = new Scene(root, 980, 640);
        stage.setTitle("Juego Final Supremo");
        stage.setScene(scene);
        stage.show();
    }

    private GridPane buildMap() {
        mapGrid = new GridPane();
        mapGrid.setHgap(4);
        mapGrid.setVgap(4);
        mapGrid.setAlignment(Pos.CENTER);
        return mapGrid;
    }

    private VBox buildSidePanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(0, 0, 0, 12));
        panel.setPrefWidth(280);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        inventoryList = new ListView<String>();
        inventoryList.setPrefHeight(120);

        HBox directionRow = new HBox(6,
                directionButton("Arriba", Direction.UP),
                directionButton("Abajo", Direction.DOWN),
                directionButton("Izq", Direction.LEFT),
                directionButton("Der", Direction.RIGHT));

        Button move = actionButton("Mover", new Runnable() {
            public void run() {
                doAction("mover", new GameRunnable() {
                    public void run() throws GameException {
                        engine.move(selectedDirection);
                    }
                });
            }
        });
        Button attack = actionButton("Atacar", new Runnable() {
            public void run() {
                doAction("atacar", new GameRunnable() {
                    public void run() throws GameException {
                        engine.attack(selectedDirection);
                    }
                });
            }
        });
        Button pick = actionButton("Recoger", new Runnable() {
            public void run() {
                doAction("recoger", new GameRunnable() {
                    public void run() throws GameException {
                        engine.pickUp(selectedDirection);
                    }
                });
            }
        });
        Button door = actionButton("Abrir puerta", new Runnable() {
            public void run() {
                doAction("abrir puerta", new GameRunnable() {
                    public void run() throws GameException {
                        engine.openDoor(selectedDirection);
                    }
                });
            }
        });
        Button use = actionButton("Usar objeto", new Runnable() {
            public void run() {
                final int index = inventoryList.getSelectionModel().getSelectedIndex();
                doAction("usar objeto", new GameRunnable() {
                    public void run() throws GameException {
                        engine.useInventoryItem(index);
                    }
                });
            }
        });

        Button save = actionButton("Guardar JSON", new Runnable() {
            public void run() {
                try {
                    new GameJsonRepository().save(engine.getState(), "partida-guardada.json");
                    engine.getState().getLog().add("Partida guardada en partida-guardada.json");
                    refresh();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        });

        panel.getChildren().addAll(new Label("Estado"), statusLabel, new Label("Inventario"), inventoryList,
                new Label("Direccion"), directionRow, move, attack, pick, door, use, save);
        return panel;
    }

    private TextArea buildLogPanel() {
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(8);
        return logArea;
    }

    private Button directionButton(String text, final Direction direction) {
        Button button = new Button(text);
        button.setOnAction(event -> {
            selectedDirection = direction;
            refresh();
        });
        return button;
    }

    private Button actionButton(String text, final Runnable runnable) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(button, Priority.NEVER);
        button.setOnAction(event -> runnable.run());
        return button;
    }

    private void doAction(String action, GameRunnable runnable) {
        try {
            runnable.run();
            refresh();
            if (engine.getState().isFinished()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fin de partida");
                alert.setHeaderText(engine.getState().isWon() ? "Victoria" : "Derrota");
                alert.setContentText(engine.getState().getLog().asText());
                alert.showAndWait();
            }
        } catch (GameException ex) {
            engine.getState().getLog().add("Error al intentar " + action + ": " + ex.getMessage());
            refresh();
            showError(ex.getMessage());
        }
    }

    private void refresh() {
        GameState state = engine.getState();
        Room room = state.getRoom();
        mapGrid.getChildren().clear();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                Button cellButton = new Button(symbolFor(position));
                cellButton.setMinSize(58, 58);
                cellButton.setMaxSize(58, 58);
                cellButton.setStyle(styleFor(room.getCell(position), position));
                mapGrid.add(cellButton, column, row);
            }
        }
        statusLabel.setText("Jugador: " + state.getPlayer().getName()
                + "\nVida: " + state.getPlayer().getHealth()
                + "\nAtaque: " + state.getPlayer().getAttackPower()
                + "\nMovimiento: " + state.getPlayer().getMovementPower()
                + "\nPosicion: " + state.getPlayer().getPosition()
                + "\nDireccion: " + selectedDirection);

        inventoryList.getItems().clear();
        MyList<GameObject> inventory = state.getPlayer().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            inventoryList.getItems().add(inventory.get(i).getName());
        }
        logArea.setText(state.getLog().asText());
    }

    private String symbolFor(Position position) {
        GameState state = engine.getState();
        if (state.getPlayer().getPosition().equals(position)) {
            return "J";
        }
        Cell cell = state.getRoom().getCell(position);
        if (cell.isWall()) {
            return "#";
        }
        if (cell.isDoor()) {
            return cell.isOpen() ? "/" : "D";
        }
        if (cell.getEnemy() != null) {
            return "E";
        }
        if (cell.getObject() != null) {
            return "O";
        }
        if (cell.hasTrap()) {
            return "T";
        }
        return ".";
    }

    private String styleFor(Cell cell, Position position) {
        if (engine.getState().getPlayer().getPosition().equals(position)) {
            return "-fx-background-color: #1f7a5c; -fx-text-fill: white; -fx-font-weight: bold;";
        }
        if (cell.isWall()) {
            return "-fx-background-color: #2f2f35; -fx-text-fill: white;";
        }
        if (cell.getEnemy() != null) {
            return "-fx-background-color: #8f2d2d; -fx-text-fill: white;";
        }
        if (cell.getObject() != null) {
            return "-fx-background-color: #b8842f; -fx-text-fill: white;";
        }
        if (cell.isDoor()) {
            return "-fx-background-color: #5d4a8f; -fx-text-fill: white;";
        }
        if (cell.hasTrap()) {
            return "-fx-background-color: #c2b280; -fx-text-fill: #222;";
        }
        return "-fx-background-color: #e9ecef; -fx-text-fill: #222;";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Accion no valida");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private interface GameRunnable {
        void run() throws GameException;
    }
}
