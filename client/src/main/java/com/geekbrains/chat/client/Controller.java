package com.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    TextArea textArea;

    @FXML
    TextField msgField;

    private Network network;
    public void sendMsg(ActionEvent actionEvent) {
        try{
            network.sendMsg(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        }catch (IOException e){
            Alert alert= new Alert(AlertType.WARNING,"Не удалось отправить сообщение. Проверьте подключение к серверу.");
            alert.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
try{
    network=new Network(8189);
    new Thread(()->{
        try {
            while (true){
                        String msg = network.returnMsg();
                        if(msg.equals("/end")){
                            network.close();
                        }
                Platform.runLater(()-> {
                    textArea.appendText(msg + "\n");
                });
            }
                    } catch (IOException e) {
                        Platform.runLater(()->{
                            Alert alert= new Alert(AlertType.WARNING,"Соединение с сервером разорвано");
                            alert.show();
                        });
                    }finally {
            network.close();
        }
    }).start();
} catch (IOException e) {
    throw new RuntimeException("Невозможно подключиться к серверу");
}
    }
}
