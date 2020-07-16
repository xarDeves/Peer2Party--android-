package com.cups.splashin.peer2party.networker.helper;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class StaticHelper {

    private final static String  SOFTWARE_IDENTIFIER = "Peer2Peer";

    private final static int UDP_DECLARE_PEERS_PORT = 13100;
    private final static int UDP_DECLARE_MSG_PORT   = 13200;

    private StaticHelper() {}

    public static byte[] convertLongToByteArray(@NotNull Long i){
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);

        byteBuffer.putLong(0, i);
        byteBuffer.clear();
        return byteBuffer.array();
    }

    public static byte[] convertIntToByteArray(int i){
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);

        byteBuffer.putInt(0, i);
        byteBuffer.clear();
        return byteBuffer.array();
    }

    public static String convertToLegalName(@NotNull String s){
        if (s.matches("[a-zA-Z]+"))
            return s.trim().replace(",", "").replace(" ","");
        return "";
    }

    public static String getSoftwareIdentifier() {
        return SOFTWARE_IDENTIFIER;
    }

    public static int getUdpDeclarePeersPort() {
        return UDP_DECLARE_PEERS_PORT;
    }

    public static int getUdpDeclareMsgPort() {
        return UDP_DECLARE_MSG_PORT;
    }
}
