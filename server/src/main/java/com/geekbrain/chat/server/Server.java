package com.geekbrain.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }
   public Server(int port) {//создаётся один раз в мейне
        try (ServerSocket serverSocket = new ServerSocket(port)) {//пробуем подключиться, отлавливаем ексепшены
            authService = new BaseAuthService();//создаём объект BaseAuthService. В объекте лежат 3 доступных пользователя
            authService.start();//просто вывели сообщение что мы ждём очередного пользователя
            clients = new ArrayList<>();//создали 1 массив клиентов...далее ьудем работать с ним
            while (true) {//в цикле ждём новых клиентов
                System.out.println("Сервер ожидает подключения...");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключён.");
                new ClientHandler(this, socket);//просто добавили нового клиента(Это совсем не просто!)
            }//добавили, вот и молодцы...ждём следующих
        } catch (IOException e) {
            System.out.println("Ошибка подключения");
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

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void broadcastMsg(String s) {
        for (ClientHandler o:clients){
            o.sendMsg(s);
        }
    }
    /*
    public ClientHandler getClient(String nick){
        for (ClientHandler o:clients){
            if(o.getName().equals(nick))
                return o;
        }
        return null;
    }
*/

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
