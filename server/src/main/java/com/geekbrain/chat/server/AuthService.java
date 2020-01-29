package com.geekbrain.chat.server;

import java.util.ArrayList;
import java.util.List;

public interface AuthService {
    void start();
    String getNickByLoginPass(String login,String pass);
    void stop();
    boolean changeNick(String name,String nick);
}
class BaseAuthService implements AuthService {//класс с интерфейсом AuthService??
    private class Entry{//Вложеный класс Entry login,pass,nick
    private String login;
    private String pass;
    private String nick;
    public Entry(String login, String pass, String nick) {
        this.login = login;
        this.pass = pass;
        this.nick = nick;
    }
}

    private List<Entry> entries;//список пользователей

    public BaseAuthService() {//создаём экземпляр BAS и заполняем его задаными пользователями
       entries= new ArrayList<>();
       entries.add(new Entry("l1","p1","User1"));
        entries.add(new Entry("l2","p2","User2"));
        entries.add(new Entry("l3","p3","User3"));
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {//проверка на правильность логина-пароля, вернём ник
        for(Entry o:entries){
            if(o.login.equals(login)&&o.pass.equals(pass))return o.nick;
        }
        return null;
    }

    @Override
    public void start() {//просто сообщение что мы запустлись?!
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {//тоже просто сообщение)
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public boolean changeNick(String name,String nick) {
        for(Entry o:entries){
            if(o.nick.equals(name)){
                o.nick=nick;
                return true;
            }
        }
        return false;
    }
}
