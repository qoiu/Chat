package com.geekbrains.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage pStage;

    public static Stage getPrimaryStage() {
        return pStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("January chat");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
        pStage=primaryStage;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
