package org.example.juegofinalsupremo.model;

public class GameState {
    private final Room room;
    private final Player player;
    private final GameLog log;
    private boolean finished;
    private boolean won;

    public GameState(Room room, Player player, GameLog log) {
        this.room = room;
        this.player = player;
        this.log = log;
    }

    public Room getRoom() {
        return room;
    }

    public Player getPlayer() {
        return player;
    }

    public GameLog getLog() {
        return log;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public void finish(boolean won) {
        this.finished = true;
        this.won = won;
    }
}
