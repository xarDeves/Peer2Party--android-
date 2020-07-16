package com.cups.splashin.peer2party.networker.singleton.data;

import java.net.Socket;

public class Peer {

    private final String IP;
    private final int PORT;
    private final String ALIAS;

    private final Socket socket;

    private final String key;

    private volatile boolean enabled = false;

    public Peer(String ip, int port, String alias){
        IP = ip;
        PORT = port;
        ALIAS = alias;

        socket = null;

        key = generateKey(ip, port, alias);
    }

    public Peer(String ip, int port, String alias, Socket s){
        IP = ip;
        PORT = port;
        ALIAS = alias;

        socket = s;

        key = generateKey(ip, port, alias);
    }

    public String getIP() {
        return IP;
    }

    public int getPORT() {
        return PORT;
    }

    public String getALIAS() {
        return ALIAS;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getKey(){
        return key;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean b){
        enabled = b;
    }

    private String generateKey(String ip, int port, String alias){
        return ip + port + alias;
    }
}
