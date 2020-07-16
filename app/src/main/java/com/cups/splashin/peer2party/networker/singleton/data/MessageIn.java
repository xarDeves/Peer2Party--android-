package com.cups.splashin.peer2party.networker.singleton.data;

import android.media.Image;

import java.util.LinkedList;

public class MessageIn {

    private final char type;

    private final String ip;
    private final int port;
    private final String alias;
    private final String text;
    private final LinkedList<Image> thumbnail;

    public MessageIn(char type, String ip, int port, String alias, String text, LinkedList<Image> thumbnail){
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.alias = alias;
        this.text = text;
        this.thumbnail = thumbnail;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getAlias() {
        return alias;
    }

    public String getText() {
        return text;
    }

    public LinkedList<Image> getThumbnail() {
        return thumbnail;
    }

    public char getType() {
        return type;
    }
}
