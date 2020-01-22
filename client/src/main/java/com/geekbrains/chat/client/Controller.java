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
    private boolean online=true;
    public void sendMsg(ActionEvent actionEvent) {
        if(online)
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
            while (online){
                        String msg = network.returnMsg();
                if(msg.equals("Сервер выключен")){
                    online=false;
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
        }//не понял как лучше? по идее он и так закроет соединение после exception. В таком варианте вроде без exception
        //из вопросов только maven не ясно что это, но видимо позже узнаем.
    }).start();
} catch (IOException e) {
    throw new RuntimeException("Невозможно подключиться к серверу");
}
    }
}
