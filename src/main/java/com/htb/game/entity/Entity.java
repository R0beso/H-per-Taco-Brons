package com.htb.game.entity;

import com.htb.game.core.Camera;
import com.htb.game.physics.CollisionBox;
import com.htb.game.physics.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Entity {
    public Vector2D position;
    public Vector2D velocity;
    public double width;
    public double height;
    public boolean alive;
    public boolean onGround;
    public boolean bouncesOffWalls;
    public String donorName;

    public Entity(double x, double y, double width, double height) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.width = width;
        this.height = height;
        this.alive = true;
        this.onGround = false;
    }

    public abstract void update(double delta);
    public abstract void render(GraphicsContext gc, Camera camera);

    public CollisionBox getCollisionBox() {
        return new CollisionBox(position.x, position.y, width, height);
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
}
