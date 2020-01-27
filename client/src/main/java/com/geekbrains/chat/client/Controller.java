package com.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    HBox hBox;
    @FXML
    TextArea textArea;

    @FXML
    TextField msgField,tfLogin;

    @FXML
    PasswordField pfPass;


    private Network network;
    public void sendMsg(ActionEvent actionEvent) {
        try{
            if(msgField.getText().trim().length()>0){
                String msg=msgField.getText();
                if(msg.startsWith("/")) {
                    String[] parts=msg.split(" ");
                    if(parts.length==1) {
                        textArea.appendText("Ошибка в команде\n");
                        return;
                    }
                }
                    network.sendMsg(msg);
                    msgField.clear();
                    msgField.requestFocus();
            }
        }catch (IOException e){
            Alert alert= new Alert(AlertType.WARNING,"Не удалось отправить сообщение. Проверьте подключение к серверу.");
            alert.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
try{
    network=new Network(8189);

    Thread t=new Thread(()->{
        try {
            while (true){
                String msg = network.returnMsg();
                if(msg.startsWith("/authok")){
                    String[] parts= msg.split(" " );
                    network.setAuthorized(true);
                    textArea.appendText("Вы вошли в чат как "+parts[1]+"\n");
                    msgField.setVisible(true);
                    hBox.setVisible(false);
                    Platform.runLater( () -> {
                        msgField.requestFocus();
                        Main.getPrimaryStage().setTitle(parts[1]);} );
                    break;
                }
                textArea.appendText(msg+"\n");
                Platform.runLater( () -> {
                    tfLogin.requestFocus();});
            }
            while (true){
                String msg = network.returnMsg();
                String[] parts=msg.split(" ");

                    switch (parts[0]){
                        case "/end":
                            return;
                        case "/name":
                            Platform.runLater( () -> {Main.getPrimaryStage().setTitle(parts[1]);} );
                            break;
                        default:textArea.appendText(msg + "\n");
                        break;
                    }
                }
    } catch (IOException e) {
            e.printStackTrace();
                        Platform.runLater(()->{
                            Alert alert= new Alert(AlertType.WARNING,"Соединение с сервером разорвано");
                            alert.show();
                        });
                    }
    });
    t.setDaemon(true);
    t.start();

} catch (IOException e) {
    throw new RuntimeException("Невозможно подключиться к серверу");
}
    }

    public void authStart(ActionEvent actionEvent) {
        try{
            network.sendMsg("/auth "+tfLogin.getText()+ " "+pfPass.getText());
            tfLogin.clear();
            pfPass.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
