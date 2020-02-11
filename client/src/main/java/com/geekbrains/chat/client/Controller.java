package com.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;

import java.io.*;
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

    @FXML
    ListView lwLUsers;
    private Thread timeout;
    private Network network;
    private boolean authorized;
    private void setAuthorized(boolean authorized) {
        if(!authorized)Platform.runLater(()->{
            msgField.clear();
            lwLUsers.getItems().clear();});
        if(authorized){
            timeout.interrupt();
        }else{
            timeout=new Thread(()->{
                try {
                    Thread.sleep(120000);
                    try{
                        network.sendMsg("/end");
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    network.close();
                   // System.exit(0);
                } catch (InterruptedException e) {
                    System.out.println("Авторизован");
                }
            });
        timeout.start();
        }
        this.authorized = authorized;
        msgField.setVisible(authorized);
        msgField.setManaged(authorized);
        hBox.setVisible(!authorized);
        hBox.setManaged(!authorized);
    }

    public void sendMsg(ActionEvent actionEvent) {
        try{
            if(msgField.getText().trim().length()>0){
                String msg=msgField.getText();
                if(msg.startsWith("/") && !correctCmd(msg))return;
                network.sendMsg(msg);
                msgField.clear();
                msgField.requestFocus();
            }
        }catch (IOException e){
            Alert alert= new Alert(AlertType.WARNING,"Не удалось отправить сообщение. Проверьте подключение к серверу.");
            alert.show();
        }
    }
    private boolean correctCmd(String msg){
        if(msg.startsWith("/end") && msg.length()==4)return true;
        if(msg.startsWith("/name ") && msg.trim().length()>5){
            String[] parts= msg.split(" ",2);
            if(!parts[1].startsWith("/")&&!parts[1].contains(" "))return true;
        }
        if(msg.startsWith("/w ")){
            String[] parts= msg.split(" ",3 );
            if(parts.length>2)
                if(parts[1].length()>0 && parts[2].length()>0)return true;
        }
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setAuthorized(false);
        tryConnection();
        lwLUsers.setOnMouseClicked(event -> {
            if(event.getClickCount()==2){
                msgField.setText("/w " +lwLUsers.getSelectionModel().getSelectedItem()+" ");
                msgField.requestFocus();
                msgField.selectEnd();
            }
        });
    }

    public void tryConnection(){
        try{
            network=new Network(8189);
            Thread t=new Thread(()->{
                try {
                    while (true){
                        String msg = network.returnMsg();
                        if(msg.startsWith("/authok")){
                            String[] parts= msg.split(" " );
                            setAuthorized(true);
                            network.setNick(parts[1]);
                            textArea.appendText(restoreChatFromLog());
                            textArea.appendText("Вы вошли в чат как "+parts[1]+"\n");
                            Platform.runLater( () -> {
                                msgField.requestFocus();
                                Main.getPrimaryStage().setTitle(parts[1]);} );
                            break;
                        }
                        textArea.appendText(msg+"\n");
                        Platform.runLater( () -> {
                            tfLogin.requestFocus();});
                    }
                    while (authorized){
                        String msg = network.returnMsg();
                        if(msg.equals("/end")){
                            setAuthorized(false);
                            return;
                        }
                        if(msg.startsWith("/name ")){
                            String[] parts=msg.split(" ",2);
                            network.setNick(parts[1]);
                            Platform.runLater( () -> {Main.getPrimaryStage().setTitle(parts[1]);} );
                        }else if(msg.startsWith("/cList ")){
                            String[] parts= msg.substring(7).split(" " );
                            Platform.runLater(()->{
                                lwLUsers.getItems().clear();
                                for(String p:parts){
                                    if(!network.getNick().equals(p))
                                        lwLUsers.getItems().add(p);
                                }
                            });

                        } else {
                            textArea.appendText(msg + "\n");
                            writeToLog(msg);
                        }
                    }
                } catch (IOException e) {
                   // e.printStackTrace();
                    Platform.runLater(()->{
                        Alert alert= new Alert(AlertType.WARNING,"Соединение с сервером разорвано");
                        alert.show();
                    });
                } finally {
                    setAuthorized(false);
                    network.close();
                    tryConnection();
                }
            });
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            throw new RuntimeException("Невозможно подключиться к серверу");
        }
    }

    public void authStart(ActionEvent actionEvent) {
        if(tfLogin.getText().length()>0 && pfPass.getText().length()>0)
            try{
            network.sendMsg("/auth "+tfLogin.getText()+ " "+pfPass.getText());
            tfLogin.clear();
            pfPass.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeToLog(String msg) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("history_" + network.getNick() + ".txt", true))) {
            out.write(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String restoreChatFromLog(){
        File file=new File("history_" + network.getNick() + ".txt");
        StringBuilder str=new StringBuilder();
        if(file.exists())
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String msg;
            while ((msg=in.readLine())!=null){
                System.out.println(msg);
                str.append(msg).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
