package com.htb.game.physics;

public class CollisionBox {
    public double x, y, width, height;

    public CollisionBox(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(CollisionBox other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }

    public double getRight() { return x + width; }
    public double getBottom() { return y + height; }
    public double getCenterX() { return x + width / 2; }
    public double getCenterY() { return y + height / 2; }
}
