package com.bucur.library.ui.settings;

import com.bucur.library.database.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingsLoader extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/settings.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        stage.setTitle("Settings");

        new Thread(() -> {
            DatabaseHandler.getInstance();
        }).start();
    }

}
