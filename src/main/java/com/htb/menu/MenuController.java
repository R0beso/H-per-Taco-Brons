package com.htb.menu;

import com.htb.game.core.GameScene;
import com.htb.game.core.HighScoreManager;
import com.htb.game.core.MusicManager;
import com.htb.tiktok.TikTokEvent;
import com.htb.tiktok.TikTokServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MenuController {

    @FXML
    private Label highScoreLabel;

    private Stage primaryStage;
    private ConcurrentLinkedQueue<TikTokEvent> tiktokQueue;
    private TikTokServer tiktokServer;
    private MusicManager musicManager;

    public void initialize() {
        int highScore = HighScoreManager.load();
        if (highScore > 0) {
            highScoreLabel.setText("Puntuación máxima: " + highScore);
        } else {
            highScoreLabel.setText("");
        }
        tiktokQueue = new ConcurrentLinkedQueue<>();
        musicManager = new MusicManager();
        try {
            tiktokServer = new TikTokServer(tiktokQueue, 8080);
            tiktokServer.start();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar servidor TikTok: " + e.getMessage());
        }
    }

    public void setPrimaryStage(Stage stage, javafx.scene.Scene scene) {
        this.primaryStage = stage;
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                startGame();
            }
        });
        stage.setOnCloseRequest(event -> {
            if (tiktokServer != null) tiktokServer.stop();
            musicManager.stop();
            Platform.exit();
        });
    }

    private void startGame() {
        musicManager.play();
        GameScene game = new GameScene(tiktokQueue, musicManager, () -> {
            musicManager.stop();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/htb/menu-view.fxml"));
                javafx.scene.Scene menuScene = new javafx.scene.Scene(loader.load(), 800, 750);
                MenuController controller = loader.getController();
                controller.setPrimaryStage(primaryStage, menuScene);
                primaryStage.setScene(menuScene);
                primaryStage.setTitle("Híper Taco Brons");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        game.start();
        primaryStage.setScene(game.getScene());
        primaryStage.setTitle("Híper Taco Brons - Jugando");
        primaryStage.setResizable(false);
    }
}
