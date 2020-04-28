package com.cups.splashin.peer2party.networker.functionality.execution;

import com.cups.splashin.peer2party.networker.networking.client.ClientReceiveDataRunnable;
import com.cups.splashin.peer2party.networker.networking.client.ClientSendDataRunnable;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonIOData;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionPoolRunnable implements Runnable {

    private final Object findPeersLock;
    private final Object decoderLock;

    private ExecutorService executorPool;

    private SingletonIOData ioData;
    private SingletonNetworkData networkData;

    public ExecutionPoolRunnable(Object findPeersLock, Object decoderLock) {
        this.findPeersLock = findPeersLock;
        this.decoderLock   = decoderLock;

        ioData = SingletonIOData.getInstance();
        networkData = SingletonNetworkData.getInstance();

        executorPool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Executor thread: Current socket size is " + networkData.getSizeSocketList());

            synchronized (findPeersLock) { // should also wake up by the encoder, not only the findpeers runnable
                while (!networkData.hasElementsIPPortList() && !ioData.hasElementsOutboundDataQueue()) {
                    System.out.println(ioData.hasElementsOutboundDataQueue());
                    try {
                        System.out.println("Executor thread: Waiting for queue to have elements...");
                        findPeersLock.wait();
                    } catch (InterruptedException ex) {
                        System.out.println("Executor thread: Interrupted!");
                    }
                }
            }

            handleOutboundQueue();
            handleInboundIPPortQueue();
        }
    }

    private void handleInboundIPPortQueue(){
        while (networkData.hasElementsIPPortList()){
            String[] ipport = networkData.popElementIPPortList();

            submitThreadToRead(networkData.getSocketIndexFromIPPort(
                    ipport[0],
                    Integer.parseInt(ipport[1])));
        }
        if(networkData.hasElementsIPPortList()){
            synchronized (decoderLock){
                decoderLock.notify();
            }
        }
    }

    private void handleOutboundQueue(){
        while(ioData.hasElementsOutboundDataQueue()){
            Byte[] identifier = ioData.getElementOutboundDataQueue();
            Byte[] length     = ioData.getElementOutboundDataQueue();
            Byte[] payload    = ioData.getElementOutboundDataQueue();

            for (int i = 0; i < networkData.getSizeSocketList(); i++) {
                submitThreadToSend(i, identifier, length, payload);
            }
        }
    }

    private void submitThreadToRead(int i) {
        //finish clientreceivedatarunnable to get the data, put them in the queue and maybe raise a flag in another
        // (5th) thread to get to work parsing the data
        executorPool.execute(new ClientReceiveDataRunnable(networkData.getSocket(i)));
    }

    private void submitThreadToSend(int i, Byte[] identifier, Byte [] length, Byte[] payload) {
        executorPool.execute(new ClientSendDataRunnable(networkData.getSocket(i), identifier, length, payload));
    }
}
