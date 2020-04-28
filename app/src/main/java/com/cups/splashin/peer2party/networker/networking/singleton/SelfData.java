package com.cups.splashin.peer2party.networker.networking.singleton;

class SelfData {

    private final static String  SOFTWARE_IDENTIFIER    = "Peer2Peer";
    private final static Integer UDP_DECLARE_PEERS_PORT = 13100;
    private final static Integer UDP_DECLARE_MSG_PORT   = 13200;

    private static Integer TCP_PORT;
    private static String  ALIAS;
    private static String  IP;

    SelfData(String ip, int port, String alias){
        IP = ip;
        ALIAS = alias;
        TCP_PORT = port;
    }

    Integer getUdpDeclareMsgPort() {
        return UDP_DECLARE_MSG_PORT;
    }

    String getSoftwareIdentifier() {
        return SOFTWARE_IDENTIFIER;
    }

    Integer getUdpDeclarePeersPort() {
        return UDP_DECLARE_PEERS_PORT;
    }

    Integer getTcpPort() {
        return TCP_PORT;
    }

    String getIP() {
        return IP;
    }

    String getALIAS(){
        return ALIAS;
    }

}
