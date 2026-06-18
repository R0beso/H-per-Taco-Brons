package com.htb.game.core;

import com.htb.game.physics.Vector2D;

public class Camera {
    private double x, y;
    private double viewportWidth, viewportHeight;

    public Camera(double viewportWidth, double viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.x = 0;
        this.y = 0;
    }

    public void follow(Vector2D target, double levelWidth, double levelHeight) {
        double targetX = target.x - viewportWidth / 2 + 30;
        double targetY = target.y - viewportHeight / 2;

        this.x = clamp(targetX, 0, Math.max(0, levelWidth - viewportWidth));
        this.y = clamp(targetY, 0, Math.max(0, levelHeight - viewportHeight));
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getViewportWidth() { return viewportWidth; }
    public double getViewportHeight() { return viewportHeight; }
}
