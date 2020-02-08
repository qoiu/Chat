package com.geekbrain.chat.server;

import java.sql.*;

public class DBAuthService implements AuthService{
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement ps;
    public DBAuthService() {
        try{
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:server/src/main/resources/users.db");
            stmt=connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        String nick;
        try{
            ResultSet rs= stmt.executeQuery("SELECT*FROM users WHERE name='"+login+"' AND password='"+pass+"';");
            nick=rs.getString("nick");
            rs.close();
            return nick;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }
    @Override
    public void stop() {
        disconnect();
        System.out.println("Сервис аутентификации остановлен");
    }
    @Override
    public boolean changeNick(String name,String nick) {
        try {
            PreparedStatement us=connection.prepareStatement("Update users SET nick=? WHERE nick=?");
            us.setString(1,nick);
            us.setString(2,name);
            us.executeUpdate();
            us.close();
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void disconnect() {
        try{
            if(ps!=null){
                ps.close();
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
