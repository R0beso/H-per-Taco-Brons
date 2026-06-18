package com.htb.game.entity;

import com.htb.game.core.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PowerUp extends Entity {

    private double animTimer = 0;
    private boolean collected = false;

    public PowerUp(double x, double y) {
        super(x, y, 24, 24);
    }

    @Override
    public void update(double delta) {
        if (!alive) return;
        animTimer += delta;
    }

    @Override
    public void render(GraphicsContext gc, Camera camera) {
        if (collected) return;

        double screenX = position.x - camera.getX();
        double screenY = position.y - camera.getY();

        if (screenX + width < 0 || screenX > camera.getViewportWidth() ||
            screenY + height < 0 || screenY > camera.getViewportHeight()) {
            return;
        }

        double cx = screenX + width / 2;
        double cy = screenY + height / 2;
        double radius = 12;
        double rotation = animTimer * 2;

        gc.save();
        gc.translate(cx, cy);
        gc.rotate(rotation);

        gc.setFill(Color.rgb(255, 180, 0));
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 * i - Math.PI / 2;
            double r = (i % 2 == 0) ? radius : radius * 0.5;
            xPoints[i] = Math.cos(angle) * r;
            yPoints[i] = Math.sin(angle) * r;
        }
        gc.fillPolygon(xPoints, yPoints, 10);

        gc.setFill(Color.rgb(255, 220, 50));
        gc.fillOval(-4, -4, 8, 8);

        gc.restore();
    }

    public void collect() {
        collected = true;
        alive = false;
    }
}
