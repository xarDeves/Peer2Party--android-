package com.cups.splashin.peer2party.networker.networking.singleton.data;

import android.media.Image;

import java.util.LinkedList;

public class MessageIn {

    private String text;
    private String alias;
    private LinkedList<Image> thumbnail;

    private char type;

    public MessageIn(String text, String alias, LinkedList<Image> thumbnail, char type){
        this.text = text;
        this.alias = alias;
        this.thumbnail = thumbnail;
        this.type = type;
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
