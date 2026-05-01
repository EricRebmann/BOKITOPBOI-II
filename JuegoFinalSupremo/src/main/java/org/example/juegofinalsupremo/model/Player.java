package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.MyList;

public class Player {
    private final String name;
    private int health;
    private int baseAttack;
    private int movement;
    private Position position;
    private final MyList<GameObject> inventory;

    public Player(String name, int health, int baseAttack, int movement, Position position) {
        this.name = name;
        this.health = Math.max(0, health);
        this.baseAttack = baseAttack;
        this.movement = movement;
        this.position = position;
        this.inventory = new MyList<GameObject>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getMovement() {
        return movement;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public MyList<GameObject> getInventory() {
        return inventory;
    }

    public int getAttackPower() {
        int power = baseAttack;
        for (int i = 0; i < inventory.size(); i++) {
            power += inventory.get(i).getDamageBonus();
        }
        return power;
    }

    public int getMovementPower() {
        int power = movement;
        for (int i = 0; i < inventory.size(); i++) {
            power += inventory.get(i).getMovementBonus();
        }
        return power;
    }

    public void receiveDamage(int damage) {
        health = Math.max(0, health - Math.max(0, damage));
    }

    public void heal(int amount) {
        health = Math.max(0, health + Math.max(0, amount));
    }
}
