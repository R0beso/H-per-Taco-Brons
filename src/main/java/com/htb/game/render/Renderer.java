package com.htb.game.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer {

    public static void drawBackground(GraphicsContext gc, double viewportWidth, double viewportHeight, int backgroundType) {
        if (backgroundType == 2) {
            drawCaveBackground(gc, viewportWidth, viewportHeight);
        } else {
            drawDayBackground(gc, viewportWidth, viewportHeight);
        }
    }

    private static void drawDayBackground(GraphicsContext gc, double viewportWidth, double viewportHeight) {
        Color top = Color.rgb(100, 180, 255);
        Color bottom = Color.rgb(210, 240, 255);

        double steps = 20;
        for (int i = 0; i < steps; i++) {
            double t = i / steps;
            double tNext = (i + 1) / steps;
            double y = t * viewportHeight;
            double h = (tNext - t) * viewportHeight + 1;

            double r = top.getRed() + (bottom.getRed() - top.getRed()) * t;
            double g = top.getGreen() + (bottom.getGreen() - top.getGreen()) * t;
            double b = top.getBlue() + (bottom.getBlue() - top.getBlue()) * t;

            gc.setFill(new Color(r, g, b, 1.0));
            gc.fillRect(0, y, viewportWidth, h);
        }

        gc.setFill(Color.WHITE);
        gc.fillOval(80, 30, 60, 30);
        gc.fillOval(120, 50, 50, 25);
        gc.fillOval(300, 20, 70, 35);
        gc.fillOval(500, 40, 55, 28);
        gc.fillOval(650, 25, 45, 22);
    }

    private static void drawCaveBackground(GraphicsContext gc, double viewportWidth, double viewportHeight) {
        Color top = Color.rgb(10, 10, 15);
        Color bottom = Color.rgb(25, 20, 30);

        double steps = 20;
        for (int i = 0; i < steps; i++) {
            double t = i / steps;
            double tNext = (i + 1) / steps;
            double y = t * viewportHeight;
            double h = (tNext - t) * viewportHeight + 1;

            double r = top.getRed() + (bottom.getRed() - top.getRed()) * t;
            double g = top.getGreen() + (bottom.getGreen() - top.getGreen()) * t;
            double b = top.getBlue() + (bottom.getBlue() - top.getBlue()) * t;

            gc.setFill(new Color(r, g, b, 1.0));
            gc.fillRect(0, y, viewportWidth, h);
        }

        gc.setFill(Color.rgb(40, 35, 50));
        gc.fillOval(60, 25, 40, 20);
        gc.fillOval(130, 50, 30, 15);
        gc.fillOval(350, 15, 50, 25);
        gc.fillOval(480, 35, 35, 18);
        gc.fillOval(700, 20, 25, 12);
    }

    public static void drawHUD(GraphicsContext gc, int score, int coins, int lives) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, 800, 32);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", 16));
        gc.fillText("PUNTOS: " + score, 10, 22);
        gc.fillText("MONEDAS: " + coins, 250, 22);
        gc.fillText("VIDAS: " + lives, 480, 22);
    }
}
