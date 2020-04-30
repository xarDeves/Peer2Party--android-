package com.cups.splashin.peer2party.networker.networking.singleton;

import java.util.Queue;

class OutboundDataQueue {

    Queue<Byte[]> byteQueue;

    private final Object byteQueueLock = new Object();

    OutboundDataQueue(){

    }

    Byte[] getLastElement() {
        synchronized (byteQueueLock) {
            if (byteQueue.size() == 0) {
                return null;
            }
            return byteQueue.remove();
        }
    }

    void popLastElement() {
        synchronized (byteQueueLock) {
            byteQueue.remove();
        }
    }

    void pushElement(byte[] bytearray) {
        synchronized (byteQueueLock) {
            Byte[] bytes = new Byte[bytearray.length];
            int i = 0;
            for (byte b : bytearray) {
                bytes[i] = b;
                i += 1;
            }
            byteQueue.add(bytes);
        }
    }

    Integer getQueueLength() {
        synchronized (byteQueueLock) {
            return byteQueue.size();
        }
    }

    boolean hasElements(){
        synchronized (byteQueueLock){
            return byteQueue.size() > 0;
        }
    }
}
