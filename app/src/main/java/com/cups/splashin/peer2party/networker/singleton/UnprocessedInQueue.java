package com.cups.splashin.peer2party.networker.singleton;


import com.cups.splashin.peer2party.networker.singleton.data.MessageBundle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UnprocessedInQueue {

    private final BlockingQueue<MessageBundle> unprocessedDataQueue;

    UnprocessedInQueue(){
        unprocessedDataQueue = new LinkedBlockingQueue<>();
    }

    void insertUnprocessedMessage(MessageBundle bundle){
        unprocessedDataQueue.add(bundle);
    }

    MessageBundle getUnprocessedMessage(){
        return unprocessedDataQueue.poll();
    }

    boolean isEmpty(){
        return unprocessedDataQueue.isEmpty();
    }
}
