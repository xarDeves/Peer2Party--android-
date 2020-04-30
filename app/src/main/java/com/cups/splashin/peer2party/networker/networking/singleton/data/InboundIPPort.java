package com.cups.splashin.peer2party.networker.networking.singleton.data;

public class InboundIPPort {

    private String IP;
    private String PORT;

    public InboundIPPort(String ip, String port){
        IP = ip;
        PORT = port;
    }

    public String getIP() {
        return IP;
    }

    public String getPORT() {
        return PORT;
    }
}
