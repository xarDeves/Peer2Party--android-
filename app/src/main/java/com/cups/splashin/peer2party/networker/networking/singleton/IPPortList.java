package com.cups.splashin.peer2party.networker.networking.singleton;

import java.util.Queue;

import networking.singleton.data.InboundIPPort;

class IPPortList {

    Queue<InboundIPPort> ipPortQueue;

    private final Object queueLock = new Object();

    IPPortList(){

    }


    String[] getLastElement() {
        InboundIPPort ipport;

        synchronized (queueLock) {
            if (ipPortQueue.size() == 0) {
                return null;
            }

            ipport = ipPortQueue.element();
        }

        return new String[] {ipport.getIP(), ipport.getPORT()};
    }

    String[] popLastElement() {
        InboundIPPort ipport;

        synchronized (queueLock) {
            ipport = ipPortQueue.remove();
        }

        return new String[] {ipport.getIP(), ipport.getPORT()};
    }

    void pushElement(String ip, Integer port) {
        synchronized (queueLock) {
            ipPortQueue.add(new InboundIPPort(ip, port.toString()));
        }
    }

    boolean hasElements() {
        synchronized (queueLock){
            return ipPortQueue.size() > 0;
        }
    }
}
