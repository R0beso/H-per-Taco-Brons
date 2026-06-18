package com.htb.game.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class InputManager {
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public void attachToScene(Scene scene) {
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.contains(code);
    }

    public boolean isLeft() {
        return isKeyPressed(KeyCode.LEFT) || isKeyPressed(KeyCode.A);
    }

    public boolean isRight() {
        return isKeyPressed(KeyCode.RIGHT) || isKeyPressed(KeyCode.D);
    }

    public boolean isJump() {
        return isKeyPressed(KeyCode.X);
    }

    public boolean isRun() {
        return isKeyPressed(KeyCode.Z);
    }

    public void clear() {
        pressedKeys.clear();
    }
}
