package com.cups.splashin.peer2party.networker.singleton;


import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerMessengersQueue {

    BlockingQueue<Peer> peerMessengers;

    PeerMessengersQueue(){
        peerMessengers = new LinkedBlockingQueue<>();
    }

    Peer getPeerMessenger(){
        return peerMessengers.poll();
    }

    void insertPeerMessenger(Peer p){
        peerMessengers.add(p);
    }

    boolean isEmpty(){
        return peerMessengers.isEmpty();
    }

}
