package com.htb.game.entity;

import com.htb.game.core.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Platform extends Entity {

    public static final double TILE_SIZE = 50;

    private final int blockType;
    private boolean bumped;
    private boolean spawnedAfterBump;
    private boolean invisible;

    public Platform(double x, double y, int blockType) {
        super(x, y, TILE_SIZE, TILE_SIZE);
        this.blockType = blockType;
        this.bumped = false;
        this.spawnedAfterBump = false;
        this.invisible = false;
    }

    public boolean isSolid() {
        return blockType != 0;
    }

    public int getBlockType() {
        return blockType;
    }

    public boolean isBumpable() {
        return blockType == 3 || blockType == 9;
    }

    public boolean isBumped() {
        return bumped;
    }

    public void bump() {
        bumped = true;
    }

    public boolean hasSpawnedAfterBump() {
        return spawnedAfterBump;
    }

    public void setSpawnedAfterBump(boolean v) {
        spawnedAfterBump = v;
    }

    public void setInvisible(boolean v) {
        invisible = v;
    }

    @Override
    public void update(double delta) {
    }

    @Override
    public void render(GraphicsContext gc, Camera camera) {
        if (invisible) return;

        double screenX = position.x - camera.getX();
        double screenY = position.y - camera.getY();

        if (screenX + width < 0 || screenX > camera.getViewportWidth() ||
            screenY + height < 0 || screenY > camera.getViewportHeight()) {
            return;
        }

        if (bumped) {
            drawBumped(gc, screenX, screenY);
            return;
        }

        switch (blockType) {
            case 1 -> drawGround(gc, screenX, screenY);
            case 2 -> drawBrick(gc, screenX, screenY);
            case 3 -> drawBlockI(gc, screenX, screenY);
            case 4 -> drawPipeLeft(gc, screenX, screenY);
            case 5 -> drawPipeRight(gc, screenX, screenY);
            case 6 -> drawStone(gc, screenX, screenY);
            case 7 -> drawSpecial(gc, screenX, screenY);
            case 9 -> drawBlockT(gc, screenX, screenY);
            case 10 -> drawDarkBrick(gc, screenX, screenY);
            case 11 -> drawDarkPipeLeft(gc, screenX, screenY);
            case 12 -> drawDarkPipeRight(gc, screenX, screenY);
            case 13 -> drawLetterBlock(gc, screenX, screenY, "A");
            case 14 -> drawLetterBlock(gc, screenX, screenY, "B");
            case 15 -> drawLetterBlock(gc, screenX, screenY, "C");
            case 16 -> drawLetterBlock(gc, screenX, screenY, "D");
            default -> drawUnknown(gc, screenX, screenY);
        }
    }

    private void drawBumped(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(100, 90, 70));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(70, 60, 40));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
    }

    private void drawGround(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(101, 67, 33));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.rgb(160, 120, 60));
        gc.fillRect(x + 4, y + 4, 8, 8);
        gc.fillRect(x + 28, y + 12, 8, 8);
        gc.fillRect(x + 14, y + 30, 8, 8);
    }

    private void drawBrick(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(200, 140, 80));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.rgb(160, 100, 50));
        gc.setLineWidth(1);
        gc.strokeLine(x, y + height / 2, x + width, y + height / 2);
        gc.strokeLine(x + width / 2, y, x + width / 2, y + height / 2);

        gc.strokeLine(x, y+ 50  , x + 50, y+ 50);

        gc.setFill(Color.rgb(200, 140, 80));
        gc.strokeLine(x, y + 25 , x, y + 50);
    }

    private void drawBlockI(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(180, 130, 50));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(130, 90, 20));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28));
        gc.fillText(" i", x + 14, y + 36);
    }

    private void drawBlockT(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(220, 130, 40));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(170, 90, 20));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", javafx.scene.text.FontWeight.BOLD, 24));
        gc.fillText("T", x + 14, y + 36);
    }

    private void drawPipeLeft(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(0, 150, 50));
        gc.fillRect(x, y, width - 10, height);
        gc.setFill(Color.rgb(0, 130, 40));
        gc.fillRect(x, y, width - 10, 6);
        gc.setFill(Color.rgb(0, 180, 60));
        gc.fillRect(x, y, width - 14, height);
    }

    private void drawPipeRight(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(0, 150, 50));
        gc.fillRect(x + 10, y, width - 10, height);
        gc.setFill(Color.rgb(0, 130, 40));
        gc.fillRect(x + 10, y, width - 10, 6);
        gc.setFill(Color.rgb(0, 180, 60));
        gc.fillRect(x + 14, y, width - 14, height);
    }

    private void drawStone(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(120, 120, 130));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(90, 90, 100));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.rgb(140, 140, 150));
        gc.fillOval(x + 8, y + 8, 12, 12);
        gc.fillOval(x + 28, y + 28, 10, 10);
    }

    private void drawSpecial(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(230, 150, 30));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(180, 110, 10));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", javafx.scene.text.FontWeight.BOLD, 16));
        gc.fillText("CP", x + 10, y + 32);
    }

    private void drawDarkBrick(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(100, 60, 30));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.rgb(70, 40, 15));
        gc.setLineWidth(1);
        gc.strokeLine(x, y + height / 2, x + width, y + height / 2);
        gc.strokeLine(x + width / 2, y, x + width / 2, y + height / 2);

        gc.strokeLine(x, y+ 50  , x + 50, y+ 50);

        gc.setFill(Color.rgb(100, 60, 30));
        gc.strokeLine(x, y + 25 , x, y + 50);

    }

    private void drawDarkPipeLeft(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(0, 90, 40));
        gc.fillRect(x, y, width - 10, height);
        gc.setFill(Color.rgb(0, 70, 30));
        gc.fillRect(x, y, width - 10, 6);
        gc.setFill(Color.rgb(0, 110, 50));
        gc.fillRect(x, y, width - 14, height);
    }

    private void drawDarkPipeRight(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(0, 90, 40));
        gc.fillRect(x + 10, y, width - 10, height);
        gc.setFill(Color.rgb(0, 70, 30));
        gc.fillRect(x + 10, y, width - 10, 6);
        gc.setFill(Color.rgb(0, 110, 50));
        gc.fillRect(x + 14, y, width - 14, height);
    }

    private void drawLetterBlock(GraphicsContext gc, double x, double y, String letter) {
        gc.setFill(Color.rgb(140, 110, 180));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.rgb(100, 70, 140));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", javafx.scene.text.FontWeight.BOLD, 28));
        gc.fillText(letter, x + 14, y + 36);
    }

    private void drawUnknown(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(255, 0, 255));
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", javafx.scene.text.FontWeight.BOLD, 16));
        gc.fillText("?", x + 16, y + 32);
    }
}
