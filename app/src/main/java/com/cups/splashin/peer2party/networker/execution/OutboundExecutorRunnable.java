package com.cups.splashin.peer2party.networker.execution;

import com.cups.splashin.peer2party.networker.client.ClientSendDataRunnable;
import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.MessageBundle;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.util.ArrayList;

public class OutboundExecutorRunnable extends ExecutorRunnable implements Runnable {

    private final IODataSingleton ioData = IODataSingleton.getInstance();
    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();

    private final Object msgLock = ioData.getOutboundQueueLock();

    public OutboundExecutorRunnable() {}

    @Override
    public void run() {
        while (true) {
            synchronized (msgLock) {
                while (ioData.isEmptyOutboundQueue()) {
                    System.out.println("Outbound Executor thread: Waiting for queue to have elements...");
                    try {
                        msgLock.wait();
                    } catch (InterruptedException ex) {
                        System.out.println("Outbound Executor thread: Interrupted!");
                    }
                }
            }

            handleOutboundQueue();
        }
    }

    private void handleOutboundQueue(){
        //change later to send only to peers selected by the user
        System.out.println("Executor Outbound Thread: Sending data to peers");
        ArrayList<Peer> peers = networkData.getAllPeers();
        MessageBundle bundle = ioData.getOutboundMessage();

        for (Peer p : peers) {
            if (p.isEnabled())
                submitThreadToSend(p, bundle);
        }
    }

    private void submitThreadToSend(Peer p, MessageBundle b) {
        submitThread(new ClientSendDataRunnable(p,b));
    }
}
