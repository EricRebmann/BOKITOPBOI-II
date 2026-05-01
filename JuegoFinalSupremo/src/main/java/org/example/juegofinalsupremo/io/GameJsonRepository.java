package org.example.juegofinalsupremo.io;

import org.example.juegofinalsupremo.contracts.Repository;
import org.example.juegofinalsupremo.exceptions.GameStorageException;
import org.example.juegofinalsupremo.model.Cell;
import org.example.juegofinalsupremo.model.Enemy;
import org.example.juegofinalsupremo.model.GameLog;
import org.example.juegofinalsupremo.model.GameObject;
import org.example.juegofinalsupremo.model.GameState;
import org.example.juegofinalsupremo.model.Player;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameJsonRepository implements Repository {
    public void save(GameState state, String path) throws GameStorageException {
        try {
            Files.write(Paths.get(path), toJson(state).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GameStorageException("No se pudo guardar la partida JSON", e);
        }
    }

    public GameState load(String path) throws GameStorageException {
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            int rows = readInt(json, "rows");
            int columns = readInt(json, "columns");
            int playerRow = readNestedInt(json, "player", "row");
            int playerColumn = readNestedInt(json, "player", "column");
            int health = readNestedInt(json, "player", "health");
            int attack = readNestedInt(json, "player", "attack");
            int movement = readNestedInt(json, "player", "movement");
            Room room = new Room(rows, columns);
            Player player = new Player("Heroe", health, attack, movement, new Position(playerRow, playerColumn));
            applyCells(json, room);
            return new GameState(room, player, new GameLog());
        } catch (RuntimeException e) {
            throw new GameStorageException("El JSON de partida no tiene el formato esperado", e);
        } catch (IOException e) {
            throw new GameStorageException("No se pudo leer la partida JSON", e);
        }
    }

    public String toJson(GameState state) {
        StringBuilder builder = new StringBuilder();
        Room room = state.getRoom();
        Player player = state.getPlayer();
        builder.append("{\n");
        builder.append("  \"rows\": ").append(room.getRows()).append(",\n");
        builder.append("  \"columns\": ").append(room.getColumns()).append(",\n");
        builder.append("  \"player\": {\"row\": ").append(player.getPosition().getRow())
                .append(", \"column\": ").append(player.getPosition().getColumn())
                .append(", \"health\": ").append(player.getHealth())
                .append(", \"attack\": ").append(player.getBaseAttack())
                .append(", \"movement\": ").append(player.getMovement()).append("},\n");
        builder.append("  \"inventory\": [");
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            appendObject(builder, player.getInventory().get(i));
        }
        builder.append("],\n");
        builder.append("  \"cells\": [\n");
        boolean first = true;
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Cell cell = room.getCell(new Position(row, column));
                if (cell.isWall() || cell.isDoor() || cell.hasTrap() || cell.getObject() != null || cell.getEnemy() != null) {
                    if (!first) {
                        builder.append(",\n");
                    }
                    first = false;
                    appendCell(builder, cell);
                }
            }
        }
        builder.append("\n  ],\n");
        builder.append("  \"finished\": ").append(state.isFinished()).append(",\n");
        builder.append("  \"won\": ").append(state.isWon()).append("\n");
        builder.append("}\n");
        return builder.toString();
    }

    private void appendCell(StringBuilder builder, Cell cell) {
        builder.append("    {\"row\": ").append(cell.getPosition().getRow())
                .append(", \"column\": ").append(cell.getPosition().getColumn());
        if (cell.isWall()) {
            builder.append(", \"type\": \"wall\"");
        } else if (cell.isDoor()) {
            builder.append(", \"type\": \"door\", \"open\": ").append(cell.isOpen());
        } else if (cell.hasTrap()) {
            builder.append(", \"type\": \"trap\", \"damage\": ").append(cell.getTrapDamage());
        } else if (cell.getObject() != null) {
            builder.append(", \"type\": \"object\", \"object\": ");
            appendObject(builder, cell.getObject());
        } else if (cell.getEnemy() != null) {
            builder.append(", \"type\": \"enemy\", \"enemy\": {\"id\": \"")
                    .append(escape(cell.getEnemy().getId())).append("\", \"name\": \"")
                    .append(escape(cell.getEnemy().getName())).append("\", \"health\": ")
                    .append(cell.getEnemy().getHealth()).append(", \"attack\": ")
                    .append(cell.getEnemy().getAttack()).append("}");
        }
        builder.append("}");
    }

    private void appendObject(StringBuilder builder, GameObject object) {
        builder.append("{\"id\": \"").append(escape(object.getId()))
                .append("\", \"name\": \"").append(escape(object.getName()))
                .append("\", \"healing\": ").append(object.getHealing())
                .append(", \"damageBonus\": ").append(object.getDamageBonus())
                .append(", \"movementBonus\": ").append(object.getMovementBonus()).append("}");
    }

    private void applyCells(String json, Room room) {
        Matcher matcher = Pattern.compile("\\{\\s*\\\"row\\\"\\s*:\\s*(\\d+)\\s*,\\s*\\\"column\\\"\\s*:\\s*(\\d+)(.*?)\\}").matcher(section(json, "cells"));
        while (matcher.find()) {
            Position position = new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            Cell cell = room.getCell(position);
            String body = matcher.group(3);
            String type = readString(body, "type");
            if ("wall".equals(type)) {
                cell.setWall(true);
            } else if ("door".equals(type)) {
                cell.setDoor(true, body.contains("\"open\": true"));
            } else if ("trap".equals(type)) {
                cell.setTrapDamage(readInt(body, "damage"));
            } else if ("object".equals(type)) {
                cell.setObject(new GameObject(readString(body, "id"), readString(body, "name"),
                        readInt(body, "healing"), readInt(body, "damageBonus"), readInt(body, "movementBonus")));
            } else if ("enemy".equals(type)) {
                cell.setEnemy(new Enemy(readString(body, "id"), readString(body, "name"),
                        readInt(body, "health"), readInt(body, "attack")));
            }
        }
    }

    private int readInt(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(-?\\d+)").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Falta entero: " + key);
        }
        return Integer.parseInt(matcher.group(1));
    }

    private int readNestedInt(String json, String section, String key) {
        return readInt(section(json, section), key);
    }

    private String readString(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Falta texto: " + key);
        }
        return matcher.group(1);
    }

    private String section(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start < 0) {
            throw new IllegalArgumentException("Falta seccion: " + key);
        }
        int brace = json.indexOf('{', start);
        int bracket = json.indexOf('[', start);
        int sectionStart = brace >= 0 && (bracket < 0 || brace < bracket) ? brace : bracket;
        char open = json.charAt(sectionStart);
        char close = open == '{' ? '}' : ']';
        int depth = 0;
        for (int i = sectionStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == open) {
                depth++;
            } else if (c == close) {
                depth--;
                if (depth == 0) {
                    return json.substring(sectionStart, i + 1);
                }
            }
        }
        throw new IllegalArgumentException("Seccion incompleta: " + key);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
