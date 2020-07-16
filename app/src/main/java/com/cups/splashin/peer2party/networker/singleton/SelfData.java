package com.cups.splashin.peer2party.networker.singleton;

class SelfData {

    private final int TCP_PORT;
    private final String  ALIAS;
    private final String  IP;

    SelfData(String ip, int port, String alias){
        IP = ip;
        ALIAS = alias;
        TCP_PORT = port;
    }

    int getTcpPort() {
        return TCP_PORT;
    }

    String getIP() {
        return IP;
    }

    String getALIAS(){
        return ALIAS;
    }

}
