package com.cups.splashin.peer2party.networker.functionality;


import android.util.Log;

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
    private final Object decoderLock = new Object();
    private Object swingWorkerLock;
    private String ipAddress;

    Model(String username, Object swingWorkerLock, String ipAddress) {
        this.ipAddress = ipAddress;
        initializeServerVariables(username, swingWorkerLock);
    }

    void startNetworking() {
        startDecoderThread();
        startServerSocketThread();
        startExecutorPoolThread();
        startBroadcastingFindMsgrThread();
        startBroadcastingFindPeersThread();
    }

    private void initializeServerVariables(String username, Object swingWorkerLock) {
        username = StaticHelper.convertToLegalName(username);
        //TODO that guy is redundant:
        if (username.equals("")) {
            throw new ExceptionInInitializerError("Invalid username");
        }

        try {
            String inetAddress = getTrueOutputAddress();
            serverSocket = new ServerSocket(0, 0, InetAddress.getByName(inetAddress));

            networkData = SingletonNetworkData.getInstance(
                    inetAddress,
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

    private String getTrueOutputAddress() throws IOException {
        //credit later
        //https://stackoverflow.com/a/2381398/10007109
        /*Socket s = new Socket("www.google.com", 80);
        String address = s.getLocalAddress().getHostAddress();
        s.close();*/

        Log.d("networker", "Model: network address is " + ipAddress);
        return ipAddress;
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

    private void startDecoderThread() {
        new Thread(new DecoderRunnable(decoderLock, swingWorkerLock)).start();
    }

    private void startExecutorPoolThread() {
        new Thread(new ExecutionPoolRunnable(findPeersLock, decoderLock)).start();
    }

    void sendMessage(byte identifier, byte[] payload) {
        System.out.println("Main thread: pushing bytes");
        Long length = (long) payload.length;

        ioData.pushElementOutboundDataQueue(new byte[]{identifier});
        ioData.pushElementOutboundDataQueue(StaticHelper.convertLongToByteArray(length));
        ioData.pushElementOutboundDataQueue(payload);                     //testing only
        Broadcaster.closePeerMsgSocket();
        Log.d("networker", "Main thread: OutboundQueue len is " + ioData.getQueueLengthOutboundDataQueue());     //testing only
    }

    String[][] getAllNodeAliasPort() {
        return networkData.getAllNodeAliasPort();
    }
}
