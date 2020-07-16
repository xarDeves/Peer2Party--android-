package com.cups.splashin.peer2party.networker.singleton;


import com.cups.splashin.peer2party.networker.singleton.data.MessageBundle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class OutboundDataQueue {

    BlockingQueue<MessageBundle> byteQueue;

    OutboundDataQueue(){
        byteQueue = new LinkedBlockingQueue<>();
    }

    MessageBundle getLastElement(){
        return byteQueue.poll();
    }

    void insertOutboundQueue(MessageBundle data){
        byteQueue.add(data);
    }

    int getOutboundQueueSize(){
        return byteQueue.size();
    }

    boolean isEmpty(){
        return byteQueue.isEmpty();
    }
}
