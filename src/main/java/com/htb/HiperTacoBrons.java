package com.htb;

import com.htb.menu.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HiperTacoBrons extends Application {

    static {
        NativeLibraryLoader.loadLibraries();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(HiperTacoBrons.class.getResource("/com/htb/menu-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 750);

        MenuController controller = loader.getController();
        controller.setPrimaryStage(stage, scene);

        stage.setTitle("Híper Taco Brons");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
