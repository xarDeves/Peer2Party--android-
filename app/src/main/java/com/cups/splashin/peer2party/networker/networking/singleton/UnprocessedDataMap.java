package com.cups.splashin.peer2party.networker.networking.singleton;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

class UnprocessedDataMap {
    ConcurrentHashMap<String, byte[]> byteMap;

    UnprocessedDataMap(){

    }

    void insertElement(@NotNull String key, byte[] val){
        byteMap.put(key, val);
    }

    void removeElement(@NotNull String key){
        byteMap.remove(key);
    }

    byte[] getElement(@NotNull String key){
        return byteMap.get(key);
    }

    boolean hasElements(){
        return byteMap.size() > 0;
    }

    Enumeration<String> getKeys(){
        return byteMap.keys();
    }
}
