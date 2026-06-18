package com.htb.game.physics;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double len = length();
        if (len == 0) return new Vector2D(0, 0);
        return new Vector2D(x / len, y / len);
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addLocal(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }
}
