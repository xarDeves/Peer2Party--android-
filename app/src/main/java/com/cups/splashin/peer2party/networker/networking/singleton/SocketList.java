package com.cups.splashin.peer2party.networker.networking.singleton;

import java.net.Socket;
import java.util.List;

class SocketList {

    List<Object> socketList;

    private Object socketListLock = new Object();

    SocketList() {

    }

    void addElement(Socket s) {
        synchronized (socketListLock) {
            socketList.add(s);
        }
    }

    void removeSocket(Socket s) {
        synchronized (socketListLock) {
            socketList.remove(s);
        }
    }

    int getSize() {
        return socketList.size();
    }

    int getSocketIndexFromIPPort(String s, int p, NodeDataList nodeData) {
        return nodeData.getIndexOfIPPort(s, p) - 1;
    }

    Socket getSocket(int i) {
        synchronized (socketListLock) {
            return (Socket) socketList.get(i);
        }
    }
}
