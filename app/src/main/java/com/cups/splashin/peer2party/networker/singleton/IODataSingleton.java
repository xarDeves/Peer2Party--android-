package com.cups.splashin.peer2party.networker.singleton;

import android.media.Image;

import com.cups.splashin.peer2party.networker.singleton.data.MessageBundle;
import com.cups.splashin.peer2party.networker.singleton.data.MessageIn;

import java.util.ArrayList;
import java.util.LinkedList;

public class IODataSingleton {

    private static IODataSingleton instance = null;

    private final OutboundDataQueue outboundQueue;

    private final UnprocessedInQueue unprocessedQueue;

    private final MessageInQueue processedQueue;

    private final Object outboundQueueLock = new Object();

    private final Object unprocessedQueueLock = new Object();

    private final Object processedQueueLock = new Object();

    private IODataSingleton(){
        unprocessedQueue = new UnprocessedInQueue();
        processedQueue = new MessageInQueue();
        outboundQueue = new OutboundDataQueue();
    }

    public static IODataSingleton getInstance() {
        if (instance == null){
            instance = new IODataSingleton();
        }

        return instance;
    }

    public void insertUnprocessedMessage(char i, String ip, int port, String alias, ArrayList<byte[]> data){
        unprocessedQueue.insertUnprocessedMessage(new MessageBundle(i, ip, port, alias, data));
        
        synchronized (unprocessedQueueLock){
            unprocessedQueueLock.notifyAll();
        }
    }

    public void insertUnprocessedMessage(char i, String ip, int port, String alias, ArrayList<byte[]> data, int size){
        unprocessedQueue.insertUnprocessedMessage(new MessageBundle(i, ip, port, alias, data, size));
        
        synchronized (unprocessedQueueLock){
            unprocessedQueueLock.notifyAll();
        }
    }
    
    public MessageBundle getUnprocessedMessage(){
        return unprocessedQueue.getUnprocessedMessage();
    }
    
    public boolean isEmptyUnprocessedQueue(){
        return unprocessedQueue.isEmpty();
    }

    public void insertProcessedMessage(char i, String ip, int port, String alias, String text, LinkedList<Image> tn){
        processedQueue.insertProcessedMessage(new MessageIn(i, ip, port, alias, text, tn));

        synchronized (processedQueueLock){
            processedQueueLock.notifyAll();
        }
    }

    public MessageIn getProcessedMessage(){
        return processedQueue.getProcessedMessage();
    }

    public boolean isEmptyProcessedQueue(){
        return processedQueue.isEmpty();
    }

    public void insertOutboundMessage(char i, String ip, int port, String alias, ArrayList<byte[]> data){
        outboundQueue.insertOutboundQueue(new MessageBundle(i, ip, port, alias, data));
        
        synchronized(outboundQueueLock) {
            outboundQueueLock.notifyAll();
        }
    }

    public void insertOutboundMessage(char i, String ip, int port, String alias, byte[] data){
        outboundQueue.insertOutboundQueue(new MessageBundle(i, ip, port, alias, data));

        synchronized(outboundQueueLock) {
            outboundQueueLock.notifyAll();
        }
    }
    
    public void insertOutboundMessage(char i, String ip, int port, String alias, ArrayList<byte[]> data, int size){
        outboundQueue.insertOutboundQueue(new MessageBundle(i, ip, port, alias, data, size));

        synchronized(outboundQueueLock) {
            outboundQueueLock.notifyAll();
        }
    }

    public MessageBundle getOutboundMessage(){
        return outboundQueue.getLastElement();
    }

    public boolean isEmptyOutboundQueue(){
        return outboundQueue.isEmpty();
    }

    public Object getOutboundQueueLock() {
        return outboundQueueLock;
    }

    public Object getUnprocessedQueueLock() {
        return unprocessedQueueLock;
    }

    public Object getProcessedQueueLock() {
        return processedQueueLock;
    }
}
