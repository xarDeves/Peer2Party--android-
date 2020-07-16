package com.cups.splashin.peer2party.networker.singleton;


import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

class PeerMap {

    ConcurrentHashMap<String, Peer> map;

    PeerMap(){
        map = new ConcurrentHashMap<>(256);
    }

    void addPeer(Peer peer){
        map.put(peer.getKey(), peer);
        System.out.println("Added " + peer.getKey());
    }

    void removePeer(Peer peer){
        map.remove(peer.getKey());
    }

    Peer getPeer(Peer peer){
        return map.get(peer.getKey());
    }

    boolean ifPeerExists(Peer peer) {
        return (!map.isEmpty()) && map.containsKey(peer.getKey());
    }

    boolean isEmpty(){
        return map.isEmpty();
    }

    int getSize(){
        return map.size();
    }

    synchronized ArrayList<Peer> getAllPeers(){
        Enumeration<String> keys = map.keys();
        ArrayList<Peer> peers = new ArrayList<>();

        String key;
        for (int i = 0; keys.hasMoreElements(); i++) {
            key = keys.nextElement();

            peers.add(map.get(key));
        }

        return peers;
    }
}
