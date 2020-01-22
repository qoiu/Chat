package com.geekbrain.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApp {
    public static void main(String[] args) throws IOException {
        try(ServerSocket serverSocket=new ServerSocket(8189)){
            System.out.println("Сервер запущен. Ожидание подключения...");
            Socket socket=serverSocket.accept();
            System.out.println("Клиент подключён.");
            DataOutputStream out=new DataOutputStream(socket.getOutputStream());
            DataInputStream in=new DataInputStream(socket.getInputStream());
            while (true){
                String x=in.readUTF();
                if(x.equals("/end")){
                    out.writeUTF("сервер выключен");
                    out.writeUTF("/end");
                    Thread.sleep(2000);
                    out.close();
                    in.close();
                    socket.close();
                    System.exit(0);
                }else{
                    out.writeUTF("echo: "+x);
                }


            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }


    }
}
