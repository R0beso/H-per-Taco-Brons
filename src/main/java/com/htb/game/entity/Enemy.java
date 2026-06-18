package com.htb.game.entity;

import com.htb.game.core.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Enemy extends Entity {

    private static final double SPEED = 60;
    private static final double DEATH_DURATION = 0.6;

    private boolean dying;
    private double deathTimer;

    public Enemy(double x, double y) {
        super(x, y, 36, 40);
        velocity.x = -SPEED;
        bouncesOffWalls = true;
    }

    @Override
    public void update(double delta) {
        if (!alive) return;
        if (dying) {
            deathTimer -= delta;
            if (deathTimer <= 0) {
                alive = false;
            }
            return;
        }
    }

    public void stomp() {
        dying = true;
        deathTimer = DEATH_DURATION;
        velocity.x = 0;
        velocity.y = -200;
    }

    public boolean isDying() {
        return dying;
    }

    @Override
    public void render(GraphicsContext gc, Camera camera) {
        double screenX = position.x - camera.getX();
        double screenY = position.y - camera.getY();

        if (screenX + width < 0 || screenX > camera.getViewportWidth() ||
            screenY + height < 0 || screenY > camera.getViewportHeight()) {
            return;
        }

        if (dying) {
            renderDying(gc, screenX, screenY);
            return;
        }

        gc.setFill(Color.rgb(180, 40, 40));
        gc.fillRoundRect(screenX + 2, screenY + 10, width - 4, height - 14, 6, 6);

        gc.setFill(Color.rgb(140, 20, 20));
        gc.fillOval(screenX, screenY, width, 14);

        gc.setFill(Color.WHITE);
        gc.fillOval(screenX + 6, screenY + 14, 8, 8);
        gc.fillOval(screenX + 22, screenY + 14, 8, 8);

        gc.setFill(Color.rgb(200, 50, 50));
        gc.fillOval(screenX + 7, screenY + 16, 5, 5);
        gc.fillOval(screenX + 23, screenY + 16, 5, 5);

        gc.setFill(Color.rgb(100, 20, 20));
        gc.fillRect(screenX + 4, screenY + height - 8, 10, 8);
        gc.fillRect(screenX + 22, screenY + height - 8, 10, 8);

        if (donorName != null) {
            gc.setFont(Font.font("Monospaced", 50));
            gc.setFill(Color.WHITE);
            double tw = donorName.length() * 28;
            gc.fillText(donorName, screenX + width / 2 - tw / 2, screenY - 10);
        }
    }

    private void renderDying(GraphicsContext gc, double x, double y) {
        double progress = 1.0 - (deathTimer / DEATH_DURATION);
        double squashH = height * (1.0 - progress * 0.7);
        double squashY = y + (height - squashH);
        double alpha = 1.0 - progress;

        gc.setGlobalAlpha(alpha);
        gc.setFill(Color.rgb(180, 40, 40));
        gc.fillRoundRect(x + 2, squashY + 6 * (squashH / height), width - 4, squashH - 8, 6, 6);
        gc.setFill(Color.rgb(140, 20, 20));
        gc.fillOval(x, squashY, width, squashH * 0.35);
        gc.setGlobalAlpha(1.0);
    }
}
