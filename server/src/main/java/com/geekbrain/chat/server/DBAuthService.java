package com.geekbrain.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DBAuthService implements AuthService{
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psGetNickByLogPass,psChangeNick;
    private static final Logger LOGGER= LogManager.getLogger(DBAuthService.class);
    public DBAuthService() {
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        try{
            psGetNickByLogPass.setString(1,login);
            psGetNickByLogPass.setString(2,pass);
            return psGetNickByLogPass.executeQuery().getString("nick");
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void start() {
        try{
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:server/src/main/resources/users.db");
            stmt=connection.createStatement();
            psChangeNick=connection.prepareStatement("Update users SET nick=? WHERE nick=?");
            psGetNickByLogPass=connection.prepareStatement("SELECT*FROM users WHERE name=? AND password=?;");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            disconnect();
        }
        LOGGER.info("Сервис аутентификации запущен");
}
    @Override
    public void stop() {
        disconnect();
        LOGGER.info("Сервис аутентификации остановен");
    }
    @Override
    public boolean changeNick(String name,String nick) {
        try {
            psChangeNick.setString(1,nick);
            psChangeNick.setString(2,name);
            psChangeNick.executeUpdate();
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void disconnect() {
        try{
            if(psChangeNick!=null){
                psChangeNick.close();
            }
            if(psGetNickByLogPass!=null){
                psGetNickByLogPass.close();
            }
            if(stmt!=null) {
                stmt.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
