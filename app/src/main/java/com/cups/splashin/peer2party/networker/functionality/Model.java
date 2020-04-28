package com.cups.splashin.peer2party.networker.functionality;

import com.cups.splashin.peer2party.networker.functionality.execution.ExecutionPoolRunnable;
import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.networking.codec.DecoderRunnable;
import com.cups.splashin.peer2party.networker.networking.server.BroadcastFindPeersRunnable;
import com.cups.splashin.peer2party.networker.networking.server.BroadcastPeerMessageRunnable;
import com.cups.splashin.peer2party.networker.networking.server.Broadcaster;
import com.cups.splashin.peer2party.networker.networking.server.ServerSocketRunnable;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonIOData;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

//TODO other than refactoring to kotlin, refactor the file tree as a final step for v1.0 completion
class Model {

    private ServerSocket serverSocket;

    private SingletonNetworkData networkData;
    private SingletonIOData ioData;

    private final Object findPeersLock = new Object();
    private final Object decoderLock   = new Object();
    private Object swingWorkerLock;

    Model(String username, Object swingWorkerLock) {
        initializeServerVariables(username, swingWorkerLock);
    }

    void startNetworking() {
        startDecoderThread();
        startServerSocketThread();
        startBroadcastingFindPeersThread();
        startBroadcastingFindMsgrThread();
        startExecutorPoolThread();
    }

    private void initializeServerVariables(String username, Object swingWorkerLock) {
        username = StaticHelper.convertToLegalName(username);
        if (username.equals("")){
            throw new ExceptionInInitializerError("Invalid username");
        }

        try {
            serverSocket = new ServerSocket(0);

            networkData  = SingletonNetworkData.getInstance(
                    InetAddress.getLocalHost().getHostAddress(),
                    serverSocket.getLocalPort(),
                    username);

            ioData = SingletonIOData.getInstance();
            this.swingWorkerLock = swingWorkerLock;

            networkData.appendNodeData(networkData.getIP(), networkData.getTcpPort(), username);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private void startBroadcastingFindPeersThread() {
        new Thread(new BroadcastFindPeersRunnable()).start();
    }

    private void startServerSocketThread() {
        new Thread(new ServerSocketRunnable(serverSocket)).start();
    }

    private void startBroadcastingFindMsgrThread() {
        new Thread(new BroadcastPeerMessageRunnable(findPeersLock)).start();
    }

    private void startDecoderThread(){
        new Thread(new DecoderRunnable(decoderLock, swingWorkerLock)).start();
    }

    private void startExecutorPoolThread() {
        new Thread(new ExecutionPoolRunnable(findPeersLock,decoderLock)).start();
    }

    void sendMessage(byte identifier, byte[] payload){
        System.out.println("Main thread: pushing bytes");
        Long length = (long) payload.length;

        ioData.pushElementOutboundDataQueue(new byte[] {identifier});
        ioData.pushElementOutboundDataQueue(StaticHelper.convertLongToByteArray(length));
        ioData.pushElementOutboundDataQueue(payload);                     //testing only
        Broadcaster.closePeerMsgSocket();
        System.out.println("Main thread: OutboundQueue len is " + ioData.getQueueLengthOutboundDataQueue());     //testing only
    }

    String[][] getAllNodeAliasPort(){
        return networkData.getAllNodeAliasPort();
    }
}