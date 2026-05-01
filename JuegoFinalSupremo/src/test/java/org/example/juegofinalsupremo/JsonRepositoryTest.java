package org.example.juegofinalsupremo;

import org.example.juegofinalsupremo.exceptions.GameStorageException;
import org.example.juegofinalsupremo.io.GameJsonRepository;
import org.example.juegofinalsupremo.model.GameEngine;
import org.example.juegofinalsupremo.model.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonRepositoryTest {
    @Test
    void serializesAndLoadsBasicState() throws GameStorageException {
        GameJsonRepository repository = new GameJsonRepository();
        GameState loaded = repository.load("src/main/resources/samples/sample-game.json");

        assertEquals(6, loaded.getRoom().getRows());
        assertEquals(8, loaded.getRoom().getColumns());
        assertEquals(20, loaded.getPlayer().getHealth());
        assertTrue(loaded.getRoom().getCell(loaded.getPlayer().getPosition()).isEmpty());
        assertTrue(repository.toJson(GameEngine.sampleGame().getState()).contains("\"player\""));
    }
}
