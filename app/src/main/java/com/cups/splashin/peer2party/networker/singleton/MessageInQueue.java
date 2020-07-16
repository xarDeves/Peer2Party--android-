package com.cups.splashin.peer2party.networker.singleton;


import com.cups.splashin.peer2party.networker.singleton.data.MessageIn;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MessageInQueue {

    BlockingQueue<MessageIn> messageIns;

    MessageInQueue(){
        messageIns = new LinkedBlockingQueue<>();
    }

    MessageIn getProcessedMessage(){
        return messageIns.poll();
    }

    void insertProcessedMessage(MessageIn msg){
        messageIns.add(msg);
    }

    boolean isEmpty(){
        return messageIns.isEmpty();
    }
}
