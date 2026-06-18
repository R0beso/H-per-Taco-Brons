package com.htb.game.entity;

import com.htb.game.core.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TacoPowerUp extends Entity {

    private static final double SPEED = 80;
    private static final double POP_SPEED = -350;

    private boolean popping = true;
    private double popTimer = 0;

    public TacoPowerUp(double x, double y) {
        super(x, y, 24, 24);
        velocity.y = POP_SPEED;
        bouncesOffWalls = true;
    }

    @Override
    public void update(double delta) {
        if (!alive) return;

        if (popping) {
            popTimer += delta;
            if (popTimer > 0.25) {
                popping = false;
                velocity.x = SPEED;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc, Camera camera) {
        double screenX = position.x - camera.getX();
        double screenY = position.y - camera.getY();

        if (screenX + width < 0 || screenX > camera.getViewportWidth() ||
            screenY + height < 0 || screenY > camera.getViewportHeight()) {
            return;
        }

        double cx = screenX + width / 2;
        double cy = screenY + height / 2;

        gc.setFill(Color.rgb(50, 150, 50));
        gc.fillOval(screenX + 2, screenY + 2, 6, 6);
        gc.fillOval(screenX + 16, screenY + 2, 6, 6);

        gc.setFill(Color.BLACK);
        gc.fillOval(screenX + 4, screenY + 4, 3, 3);
        gc.fillOval(screenX + 18, screenY + 4, 3, 3);

        gc.setFill(Color.rgb(200, 50, 50));
        gc.fillOval(screenX + 6, screenY + 2, 12, 3);

        //Carne
        gc.setFill(Color.rgb(77, 43, 13));
        gc.fillOval(screenX - 4, screenY + 6, 6, 6);
        gc.fillOval(screenX + 23, screenY + 6, 6, 6);
        gc.fillOval(screenX - 10, screenY + 12, 6, 6);
        gc.fillOval(screenX + 28, screenY + 12, 6, 6);

        //Tortilla
        gc.setFill(Color.rgb(210, 160, 80));
        gc.fillArc(screenX - 10, screenY + 4, 44, 40, 0, 180, ArcType.CHORD);

        gc.setFill(Color.rgb(160, 110, 40));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("T", screenX + width / 2 - 7, screenY + 20);

        if (donorName != null) {
            gc.setFont(Font.font("Monospaced", 50));
            gc.setFill(Color.WHITE);
            double tw = donorName.length() * 28;
            gc.fillText(donorName, screenX + width / 2 - tw / 2, screenY - 15);
        }
    }
}
