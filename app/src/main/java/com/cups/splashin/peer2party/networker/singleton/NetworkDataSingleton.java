package com.cups.splashin.peer2party.networker.singleton;

import android.util.Log;

import com.cups.splashin.peer2party.android.app.PeerTransaction;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.net.Socket;
import java.util.ArrayList;

public class NetworkDataSingleton {

    private static NetworkDataSingleton instance = null;

    private static SelfData selfData;

    private final PeerMap peers;

    private final PeerMessengersQueue peerMessengers;

    private final Object peerLock = new Object();

    private final Object peerMessengerLock = new Object();

    private NetworkDataSingleton() {
        peers = new PeerMap();
        peerMessengers = new PeerMessengersQueue();
    }

    public static NetworkDataSingleton getInstance() {
        if(instance == null){
            instance = new NetworkDataSingleton();
        }

        return instance;
    }

    public static void setSelfData(String ip, int port, String alias) {
        selfData = new SelfData(ip, port, alias);
    }

    public void insertPeer(Peer p){
        peers.addPeer(p);

        synchronized (peerLock){
            peerLock.notifyAll();
        }
    }

    public void insertPeer(String ip, int port, String alias, @NotNull Socket s) {
        peers.addPeer(new Peer(ip, port, alias, s));

        synchronized (peerLock){
            //TODO eventbus wakes, viewModel fetch "getAllPeers"
            peerLock.notifyAll();
            PeerTransaction event = new PeerTransaction();
            Log.d("fuck", "SingletonNetworkData posted");
            EventBus.getDefault().postSticky(event);
        }
    }

    public void removePeer(String ip, int port, String alias) {
        peers.removePeer(new Peer(ip, port, alias));

        synchronized (peerLock){
            peerLock.notifyAll();
            PeerTransaction event = new PeerTransaction();
            Log.d("fuck", "SingletonNetworkData posted");
            EventBus.getDefault().postSticky(event);
        }
    }

    public void removePeer(Peer p){
        peers.removePeer(p);

        synchronized (peerLock){
            peerLock.notifyAll();
            PeerTransaction event = new PeerTransaction();
            Log.d("fuck", "SingletonNetworkData posted");
            EventBus.getDefault().postSticky(event);
        }
    }

    public void insertPeerMessenger(Peer p){
        peerMessengers.insertPeerMessenger(peers.getPeer(p));

        synchronized (peerMessengerLock){
            peerMessengerLock.notifyAll();
        }
    }

    public Peer getPeerMessenger(){
        return peerMessengers.getPeerMessenger();
    }

    public boolean isPeerMessengerEmpty(){
        return peerMessengers.isEmpty();
    }

    public boolean ifPeerExists(String ip, int port, String alias) {
        return peers.ifPeerExists(new Peer(ip, port, alias));
    }

    public boolean ifPeerIsSelf(String ip, int port, String alias) {
        return ip.equals(getSelfIP()) && port == getSelfPORT() && alias.equals(getSelfAlias());
    }

    public int getPeerMapSize(){
        return peers.getSize();
    }

    public boolean isPeerMapEmpty(){
        return peers.isEmpty();
    }

    public ArrayList<Peer> getAllPeers(){
        return peers.getAllPeers();
    }

    public String getSelfIP(){
        return selfData.getIP();
    }

    public int getTcpPort(){
        return selfData.getTcpPort();
    }

    public int getSelfPORT(){
        return selfData.getTcpPort();
    }

    public String getSelfAlias(){
        return selfData.getALIAS();
    }

    public Object getPeerLock() {
        return peerLock;
    }

    public Object getPeerMessengerLock() {
        return peerMessengerLock;
    }

}
