package org.example.juegofinalsupremo.model;

public class GameObject {
    private final String id;
    private final String name;
    private final int healing;
    private final int damageBonus;
    private final int movementBonus;

    public GameObject(String id, String name, int healing, int damageBonus, int movementBonus) {
        this.id = id;
        this.name = name;
        this.healing = healing;
        this.damageBonus = damageBonus;
        this.movementBonus = movementBonus;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHealing() {
        return healing;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getMovementBonus() {
        return movementBonus;
    }

    public String toString() {
        return name;
    }
}
