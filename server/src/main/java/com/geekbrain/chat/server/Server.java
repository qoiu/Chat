package com.geekbrain.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;
    private static final Logger LOGGER= LogManager.getLogger(Server.class);
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }
   public Server(int port) {//создаётся один раз в мейне
        try (ServerSocket serverSocket = new ServerSocket(port)) {//пробуем подключиться, отлавливаем ексепшены
            authService = new DBAuthService();//создаём объект BaseAuthService. В объекте лежат 3 доступных пользователя
            authService.start();//просто вывели сообщение что мы ждём очередного пользователя
            clients = new ArrayList<>();//создали 1 массив клиентов...далее ьудем работать с ним
            while (true) {//в цикле ждём новых клиентов
                LOGGER.info("Сервер ожидает подключения...");
                Socket socket = serverSocket.accept();
                LOGGER.info("Клиент подключён.");
                new ClientHandler(this, socket);//просто добавили нового клиента(Это совсем не просто!)
            }//добавили, вот и молодцы...ждём следующих
        } catch (IOException e) {
            LOGGER.warn("Ошибка подключения");
        }
    }

        public synchronized boolean isNickBusy(String nick) {
        for(ClientHandler o:clients){
            if(o.getName().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public void whisp(ClientHandler from, String to,String msg){
        if(isNickBusy(to)){
            getClient(to).sendMsg(from.getName() +"(whisp): "+msg);
            from.sendMsg("to " + to +": "+msg);
        }else{
            from.sendMsg("Неверное имя пользователя");
        }
    }


    public synchronized void broadcastMsg(String s) {
        for (ClientHandler o:clients){
            o.sendMsg(s);
        }
    }
    public void sendClientListlist(){
        StringBuilder msg=new StringBuilder();
        msg.append("/cList ");
        for (ClientHandler o:clients){
            msg.append(o.getName()).append(" ");
        }
        msg.setLength(msg.length()-1);
        broadcastMsg(msg.toString());
    }
    public ClientHandler getClient(String nick){
        for (ClientHandler o:clients){
            if(o.getName().equals(nick))
                return o;
        }
        return null;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        sendClientListlist();
    }
    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        sendClientListlist();
    }
}
