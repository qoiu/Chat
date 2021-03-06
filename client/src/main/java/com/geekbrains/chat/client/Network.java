package com.geekbrains.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private DataInputStream in;
    private DataOutputStream out;
    Socket socket;
    private String nick;

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public Network(int port) throws IOException {
        socket=new Socket("localhost",port);
        out=new DataOutputStream(socket.getOutputStream());
        in=new DataInputStream(socket.getInputStream());
    }
    public void sendMsg(String msg) throws IOException{
        out.writeUTF(msg);
    }
    public String returnMsg() throws IOException {
        return in.readUTF();
    }
    public void close(){
        try {
            if(in!=null)in.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        if(out!=null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket!=null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
