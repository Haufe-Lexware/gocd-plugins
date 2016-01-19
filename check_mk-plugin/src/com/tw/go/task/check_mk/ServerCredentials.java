package com.tw.go.task.check_mk;

public class ServerCredentials
{
    private String Username;
    private String Password;
    private String Server;

    public ServerCredentials(String username, String password, String server) {
        Username = username;
        Password = password;
        Server = server;
    }

    public String getUsername() {
        return Username;
    }

    public String getServer() {
        return Server;
    }

    public String getPassword() {
        return Password;
    }
}
