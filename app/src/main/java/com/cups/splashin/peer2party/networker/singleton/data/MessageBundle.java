package com.cups.splashin.peer2party.networker.singleton.data;

import java.util.ArrayList;

public class MessageBundle {

    private final char identifier;

    private final String alias;

    private final String ip;

    private final int port;

    byte[] data;

    int size;

    public MessageBundle(char identifier, String ip, int port, String alias, byte[] data){
        this.identifier = identifier;
        this.ip = ip;
        this.port = port;
        this.alias = alias;
        this.size = data.length;
        this.data = data;
    }

    public MessageBundle(char identifier, String ip, int port, String alias, ArrayList<byte[]> data){
        this.identifier = identifier;
        this.ip = ip;
        this.port = port;
        this.alias = alias;
        this.size = calculateArraylistSize(data);

        this.data = convertArraylist(data, size);
    }

    public MessageBundle(char identifier, String ip, int port, String alias, ArrayList<byte[]> data, int size){
        this.identifier = identifier;
        this.ip = ip;
        this.port = port;
        this.alias = alias;
        this.size = size;

        //creates a new array that will store all the contents of the array contiguously
        this.data = convertArraylist(data, size);
    }

    private int calculateArraylistSize(ArrayList<byte[]> data){
        int size = 0;
        for (byte[] bytes : data) {
            size += bytes.length;
        }

        return size;
    }

    private byte[] convertArraylist(ArrayList<byte[]> data, int size){
        byte[] array = new byte[size];

        int offset = 0;
        for (byte[] bytes : data) {
            System.arraycopy(bytes, 0, array, offset, bytes.length);

            offset += bytes.length;
        }

        return array;
    }

    public char getIdentifier() {
        return identifier;
    }

    public String getAlias() {
        return alias;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public byte[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }
}
