package com.cups.splashin.peer2party.networker.networking.singleton;

import android.media.Image;

import com.cups.splashin.peer2party.networker.networking.singleton.data.MessageIn;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SingletonIOData {

    private static SingletonIOData instance = null;

    private static UnprocessedDataMap unprocessedDataMap;

    private static MessageInQueue messageInQueue;

    private static OutboundDataQueue outboundDataQueue;

    private SingletonIOData() {

    }

    public static SingletonIOData getInstance() {
        if (instance == null) {
            instance = new SingletonIOData();

            initInboundDataMap();
            initMessageOutQueue();
            initOutboundDataQueue();
        }
        return instance;
    }

    private static void initInboundDataMap() {
        unprocessedDataMap = new UnprocessedDataMap();
        unprocessedDataMap.byteMap = new ConcurrentHashMap<>(256);
    }

    private static void initMessageOutQueue() {
        messageInQueue = new MessageInQueue();
        messageInQueue.messageIns = new LinkedBlockingQueue<>();
    }

    private static void initOutboundDataQueue() {
        outboundDataQueue = new OutboundDataQueue();
        outboundDataQueue.byteQueue = new LinkedList<>();
    }

    public void insertElementInboundData(@NotNull String key, byte[] val) {
        unprocessedDataMap.insertElement(key, val);
    }

    public void removeElementInboundData(@NotNull String key) {
        unprocessedDataMap.removeElement(key);
    }

    public byte[] getElementInboundData(@NotNull String key) {
        return unprocessedDataMap.getElement(key);
    }

    public boolean hasElementsInboundData() {
        return unprocessedDataMap.hasElements();
    }

    public Enumeration<String> getKeysInboundData() {
        return unprocessedDataMap.getKeys();
    }

    public MessageIn getMessageProcessed() {
        return messageInQueue.getMessageIn();
    }

    public void insertMessageProcessed(String text, String alias, LinkedList<Image> thumbnail, char type) {
        messageInQueue.insertMessageIn(text, alias, thumbnail, type);
    }

    public boolean IsEmptyProcessedMessage() {
        return messageInQueue.isEmpty();
    }

    public Byte[] getElementOutboundDataQueue() {
        return outboundDataQueue.getLastElement();
    }

    public void popLastElementOutboundDataQueue() {
        outboundDataQueue.popLastElement();
    }

    public void pushElementOutboundDataQueue(byte[] bytearray) {
        outboundDataQueue.pushElement(bytearray);
    }

    public Integer getQueueLengthOutboundDataQueue() {
        return outboundDataQueue.getQueueLength();
    }

    public boolean hasElementsOutboundDataQueue() {
        return outboundDataQueue.hasElements();
    }

}
