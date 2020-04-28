package com.cups.splashin.peer2party.networker.networking.singleton;

import android.media.Image;

import com.cups.splashin.peer2party.networker.networking.singleton.data.MessageIn;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

class MessageInQueue {

    BlockingQueue<MessageIn> messageIns;

    MessageInQueue(){

    }

    MessageIn getMessageIn(){
        return messageIns.poll();
    }

    void insertMessageIn(String text, String alias, LinkedList<Image> thumbnail, char type){
        messageIns.add(new MessageIn(text, alias, thumbnail, type));
    }

    boolean isEmpty(){
        return messageIns.isEmpty();
    }
}
