package com.cups.splashin.peer2party.networker.execution;


import com.cups.splashin.peer2party.networker.client.ClientReceiveDataRunnable;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

public class InboundExecutorRunnable extends ExecutorRunnable implements Runnable {

    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();

    private final Object msgFoundLock = networkData.getPeerMessengerLock();

    public InboundExecutorRunnable() {}

    @Override
    public void run() {
        synchronized (msgFoundLock) {
            while (true) {
                while (networkData.isPeerMessengerEmpty()) {
                    System.out.println("Executor inbound thread: Waiting for queue to have elements...");
                    try {
                        msgFoundLock.wait();
                    } catch (InterruptedException ex) {
                        System.out.println("Executor inbound thread: Interrupted!");
                    }
                }
                handleInboundPeerQueue();
            }
        }
    }

    private void handleInboundPeerQueue(){
        //change later to read only to peers selected by the user
        while(!networkData.isPeerMessengerEmpty()){
            Peer p = networkData.getPeerMessenger();

            if (p.isEnabled())
                submitThreadToRead(p);
        }
    }

    private void submitThreadToRead(Peer p) {
        submitThread(new ClientReceiveDataRunnable(p));
    }
}
