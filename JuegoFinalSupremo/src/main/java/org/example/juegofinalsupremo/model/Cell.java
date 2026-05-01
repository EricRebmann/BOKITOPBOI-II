package org.example.juegofinalsupremo.model;

public class Cell {
    private final Position position;
    private boolean wall;
    private boolean door;
    private boolean open;
    private int trapDamage;
    private GameObject object;
    private Enemy enemy;

    public Cell(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isWall() {
        return wall;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public boolean isDoor() {
        return door;
    }

    public boolean isOpen() {
        return open;
    }

    public void setDoor(boolean door, boolean open) {
        ensureNoContent();
        this.door = door;
        this.open = open;
    }

    public void openDoor() {
        if (door) {
            open = true;
        }
    }

    public boolean hasTrap() {
        return trapDamage > 0;
    }

    public int getTrapDamage() {
        return trapDamage;
    }

    public int consumeTrap() {
        int damage = trapDamage;
        trapDamage = 0;
        return damage;
    }

    public void setTrapDamage(int trapDamage) {
        ensureNoContent();
        this.trapDamage = Math.max(0, trapDamage);
    }

    public GameObject getObject() {
        return object;
    }

    public void setObject(GameObject object) {
        ensureAssignable(object != null, "objeto");
        this.object = object;
    }

    public GameObject takeObject() {
        GameObject taken = object;
        object = null;
        return taken;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        ensureAssignable(enemy != null, "enemigo");
        this.enemy = enemy;
    }

    public void clearEnemy() {
        enemy = null;
    }

    public boolean isWalkable() {
        return !wall && (!door || open) && enemy == null && object == null;
    }

    public boolean isEmpty() {
        return !wall && !door && trapDamage == 0 && object == null && enemy == null;
    }

    private void ensureAssignable(boolean assigning, String type) {
        if (assigning && !isEmpty()) {
            throw new IllegalStateException("La celda " + position + " ya contiene otra entidad; no se puede poner " + type);
        }
    }

    private void ensureNoContent() {
        if (object != null || enemy != null) {
            throw new IllegalStateException("La celda " + position + " ya contiene entidad");
        }
    }
}
