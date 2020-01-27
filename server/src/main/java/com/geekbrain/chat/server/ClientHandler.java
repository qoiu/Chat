package com.geekbrain.chat.server;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(Server server, Socket socket) {
        try{
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(()->{
                try{
                    authentication();//проводим аутентификацию клиента. Входим в цикл и ждём ввода логина и пароля
                    readMessages();//теперь этот несчастный клиент вечно будет слушать чат
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    closeConnection();
                }
            }).start();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeConnection();//по-моему так? Ведь больше мы этим коннекшеном пользоваться не будем.
        }
    }
    public void authentication() throws IOException{
        while (true){
            String str=in.readUTF();//получаем сообщение с логином и паролем
            if(str.startsWith("/auth")){
                String[] parts = str.split(" ");//парсим
                String nick =server.getAuthService().getNickByLoginPass(parts[1],parts[2]);//получаем ник, если существует
                //сложная строчка. Ник= идём на сервер(мы его знаем т.к задаём при создании объекта)
                //getAuthService() - сложный механизм дающий ссылку на список доступных пользователей(в BaseAuthService)
                //там соответственно проверяем логин, пароль
                if(nick!=null){//если нулл, попробуем заново
                    if(!server.isNickBusy(nick)){//самое интересное-подключение
                        sendMsg("/authok "+nick);//возвращаем в окно клиента ник
                        name=nick;//теперь нейм данного юзера-ник)
                        server.broadcastMsg(name+" зашёл в чат");
                        server.subscribe(this);//вот тут-то мы его и подключаем к чату!!!
                        return;//Закроем цикл и пойдём дальше
                    }else{
                        sendMsg("Учтеная запись уже используется");
                    }
                }else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }//Самая весёлая часть

    public void readMessages() throws IOException{
        while (true){
            String strFromClient=in.readUTF();
            System.out.println("от" + name + ": "+strFromClient);
           /* if(strFromClient.startsWith("/w")){
                String[] parts= strFromClient.split(" " );
                server.getClient(parts[1]).sendMsg(parts[2]);
            }*/
            if(strFromClient.equals("/end")){
                return;
            }
            server.broadcastMsg(name + ": "+strFromClient);
        }
    }//читаем сообщения из чата и перечылаем всем участникам. Если \енд, то перестаём читать.

    public void sendMsg(String msg) {
        try{
            out.writeUTF(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }//послать месседж объекту этого класса
public void closeConnection(){
        server.unsubscribe(this);
        server.broadcastMsg(name+" покинул чат");
    try{
        in.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    try{
        out.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    try{
        socket.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}//закрываем соединение для объекта этого класса
}

