package org.example.juegofinalsupremo.model;

public class Enemy {
    private final String id;
    private final String name;
    private int health;
    private final int attack;

    public Enemy(String id, String name, int health, int attack) {
        this.id = id;
        this.name = name;
        this.health = Math.max(0, health);
        this.attack = attack;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void receiveDamage(int damage) {
        health = Math.max(0, health - Math.max(0, damage));
    }
}
