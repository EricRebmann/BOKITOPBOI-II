package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.contracts.GameActions;
import org.example.juegofinalsupremo.data.GridGraph;
import org.example.juegofinalsupremo.data.MyList;
import org.example.juegofinalsupremo.data.MyQueue;
import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.exceptions.InvalidActionException;
import org.example.juegofinalsupremo.exceptions.InvalidMoveException;

public class GameEngine implements GameActions {
    private final GameState state;
    private final MyQueue<String> turnQueue = new MyQueue<String>();

    public GameEngine(GameState state) {
        this.state = state;
        turnQueue.enqueue("PLAYER");
        turnQueue.enqueue("ENEMIES");
    }

    public static GameEngine sampleGame() {
        Room room = new Room(6, 8);
        Player player = new Player("Heroe", 20, 5, 3, new Position(4, 1));
        GameLog log = new GameLog();
        GameState state = new GameState(room, player, log);
        room.getCell(new Position(1, 1)).setWall(true);
        room.getCell(new Position(1, 2)).setWall(true);
        room.getCell(new Position(2, 4)).setTrapDamage(4);
        room.getCell(new Position(3, 3)).setObject(new GameObject("potion-1", "Pocion", 6, 0, 0));
        room.getCell(new Position(4, 4)).setObject(new GameObject("staff-1", "Baston ligero", 0, 1, 2));
        room.getCell(new Position(2, 6)).setEnemy(new Enemy("enemy-1", "Sombra", 10, 3));
        room.getCell(new Position(0, 7)).setDoor(true, false);
        log.add("Partida iniciada");
        return new GameEngine(state);
    }

    public GameState getState() {
        return state;
    }

    public MyList<Position> reachableCells() {
        return new GridGraph(state.getRoom()).reachable(state.getPlayer().getPosition(), state.getPlayer().getMovementPower());
    }

    public void move(Direction direction) throws GameException {
        ensurePlayable();
        Position target = state.getPlayer().getPosition().translate(direction);
        if (!state.getRoom().isValid(target)) {
            throw new InvalidMoveException("Movimiento fuera del tablero");
        }
        Cell cell = state.getRoom().getCell(target);
        if (!cell.isWalkable()) {
            throw new InvalidMoveException("No se puede entrar en " + target);
        }
        state.getPlayer().setPosition(target);
        state.getLog().add("El jugador se mueve a " + target);
        if (cell.hasTrap()) {
            int damage = cell.consumeTrap();
            state.getPlayer().receiveDamage(damage);
            state.getLog().add("El jugador cae en una trampa y recibe " + damage + " de dano");
        }
        checkEnd();
        rotateTurn();
    }

    public void attack(Direction direction) throws GameException {
        ensurePlayable();
        Cell target = adjacentCell(direction);
        Enemy enemy = target.getEnemy();
        if (enemy == null) {
            throw new InvalidActionException("No hay enemigo en esa celda");
        }
        int damage = state.getPlayer().getAttackPower();
        enemy.receiveDamage(damage);
        state.getLog().add("El jugador ataca a " + enemy.getName() + " por " + damage);
        if (!enemy.isAlive()) {
            target.clearEnemy();
            state.getLog().add(enemy.getName() + " ha sido derrotado");
        } else {
            state.getPlayer().receiveDamage(enemy.getAttack());
            state.getLog().add(enemy.getName() + " contraataca por " + enemy.getAttack());
        }
        checkEnd();
        rotateTurn();
    }

    public void pickUp(Direction direction) throws GameException {
        ensurePlayable();
        Cell target = adjacentCell(direction);
        GameObject object = target.takeObject();
        if (object == null) {
            throw new InvalidActionException("No hay objeto en esa celda");
        }
        if (isObjectIdInInventory(object.getId())) {
            target.setObject(object);
            throw new InvalidActionException("El objeto ya esta en el inventario");
        }
        state.getPlayer().getInventory().add(object);
        state.getLog().add("El jugador recoge " + object.getName());
        rotateTurn();
    }

    public void openDoor(Direction direction) throws GameException {
        ensurePlayable();
        Cell target = adjacentCell(direction);
        if (!target.isDoor()) {
            throw new InvalidActionException("No hay puerta en esa celda");
        }
        target.openDoor();
        state.getLog().add("El jugador abre una puerta");
        rotateTurn();
    }

    public void useInventoryItem(int index) throws GameException {
        ensurePlayable();
        if (index < 0 || index >= state.getPlayer().getInventory().size()) {
            throw new InvalidActionException("Objeto de inventario invalido");
        }
        GameObject object = state.getPlayer().getInventory().get(index);
        if (object.getHealing() <= 0) {
            throw new InvalidActionException("Ese objeto no es consumible ahora");
        }
        state.getPlayer().heal(object.getHealing());
        state.getPlayer().getInventory().removeAt(index);
        state.getLog().add("El jugador usa " + object.getName() + " y recupera " + object.getHealing());
        rotateTurn();
    }

    private Cell adjacentCell(Direction direction) throws InvalidActionException {
        Position target = state.getPlayer().getPosition().translate(direction);
        if (!state.getRoom().isValid(target)) {
            throw new InvalidActionException("La celda contigua no existe");
        }
        return state.getRoom().getCell(target);
    }

    private boolean isObjectIdInInventory(String id) {
        for (int i = 0; i < state.getPlayer().getInventory().size(); i++) {
            if (state.getPlayer().getInventory().get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void ensurePlayable() throws InvalidActionException {
        if (state.isFinished()) {
            throw new InvalidActionException("La partida ya ha terminado");
        }
    }

    private void checkEnd() {
        if (state.getPlayer().getHealth() <= 0) {
            state.finish(false);
            state.getLog().add("Fin de partida: derrota");
            return;
        }
        if (state.getPlayer().getPosition().equals(new Position(0, state.getRoom().getColumns() - 1))) {
            state.finish(true);
            state.getLog().add("Fin de partida: victoria");
        }
    }

    private void rotateTurn() {
        String actor = turnQueue.dequeue();
        turnQueue.enqueue(actor);
    }
}
