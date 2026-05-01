package org.example.juegofinalsupremo;

import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.model.Direction;
import org.example.juegofinalsupremo.model.GameEngine;
import org.example.juegofinalsupremo.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameEngineTest {
    @Test
    void playerMovesOnlyToValidWalkableCells() throws GameException {
        GameEngine engine = GameEngine.sampleGame();

        engine.move(Direction.UP);

        assertEquals(new Position(3, 1), engine.getState().getPlayer().getPosition());
    }

    @Test
    void invalidMovementThrowsException() {
        GameEngine engine = GameEngine.sampleGame();

        assertThrows(GameException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Throwable {
                engine.move(Direction.UP);
                engine.move(Direction.UP);
                engine.move(Direction.UP);
            }
        });
    }

    @Test
    void trapDamagesPlayerAndDisappears() throws GameException {
        GameEngine engine = GameEngine.sampleGame();
        engine.move(Direction.RIGHT);
        engine.move(Direction.UP);
        engine.move(Direction.UP);
        engine.move(Direction.RIGHT);
        engine.move(Direction.RIGHT);

        assertEquals(16, engine.getState().getPlayer().getHealth());
        assertTrue(!engine.getState().getRoom().getCell(new Position(2, 4)).hasTrap());
    }
}
