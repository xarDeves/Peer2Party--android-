package com.cups.splashin.peer2party.networker.networking.singleton.data;

public class NodeData {

    private String  IP;
    private Integer PORT;
    private String  ALIAS;

    public NodeData(String ip, Integer port, String alias){
        IP = ip;
        PORT = port;
        ALIAS = alias;
    }

    public String getIP() {
        return IP;
    }

    public Integer getPORT() {
        return PORT;
    }

    public String getALIAS() {
        return ALIAS;
    }
}
