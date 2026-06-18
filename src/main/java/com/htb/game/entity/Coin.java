package com.htb.game.entity;

import com.htb.game.core.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Coin extends Entity {

    private double bobTimer = 0;
    private double startY;

    public Coin(double x, double y) {
        super(x, y, 18, 18);
        this.startY = y;
    }

    @Override
    public void update(double delta) {
        if (!alive) return;
        bobTimer += delta;
        position.y = startY + Math.sin(bobTimer * 3) * 4;
    }

    @Override
    public void render(GraphicsContext gc, Camera camera) {
        double screenX = position.x - camera.getX();
        double screenY = position.y - camera.getY();

        if (screenX + width < 0 || screenX > camera.getViewportWidth() ||
            screenY + height < 0 || screenY > camera.getViewportHeight()) {
            return;
        }

        double pulse = 1.0 + Math.sin(bobTimer * 5) * 0.1;
        double drawW = width * pulse;
        double drawH = height / pulse;
        double cx = screenX + width / 2;
        double cy = screenY + height / 2;

        gc.setFill(Color.GOLD);
        gc.fillOval(cx - drawW / 2, cy - drawH / 2, drawW, drawH);

        gc.setFill(Color.rgb(255, 230, 100));
        gc.fillOval(cx - drawW / 2 + 3, cy - drawH / 2 + 3, drawW / 3, drawH / 3);
    }
}
