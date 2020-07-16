package com.cups.splashin.peer2party.networker;


import com.cups.splashin.peer2party.networker.codec.DecoderRunnable;
import com.cups.splashin.peer2party.networker.execution.InboundExecutorRunnable;
import com.cups.splashin.peer2party.networker.execution.OutboundExecutorRunnable;
import com.cups.splashin.peer2party.networker.server.FindPeerMessageNotifierRunnable;
import com.cups.splashin.peer2party.networker.server.FindPeersBroadcasterRunnable;
import com.cups.splashin.peer2party.networker.server.SendPeerMessageNotifierRunnable;
import com.cups.splashin.peer2party.networker.server.ServerSocketRunnable;
import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

//TODO in case of network change recreate:
public class Model {
    private ServerSocket serverSocket;

    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();
    private final IODataSingleton ioData = IODataSingleton.getInstance();

    public Model(String username, String address) {
        initializeServerVariables(username, address);
    }

    public void startNetworking() {
        startDecoderThread();
        startServerSocketThread();

        startInboundExecutorPoolThread();
        startOutboundExecutorPoolThread();

        startFindPeersBroadcasterThread();
        startFindPeerMessageNotifierThread();
        startSendPeerMessageNotifierThread();
    }

    private void initializeServerVariables(String username, String address) {
        try {
            serverSocket = new ServerSocket(0, 0, InetAddress.getByName(address));
            NetworkDataSingleton.setSelfData(address, serverSocket.getLocalPort(), username);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private void startFindPeersBroadcasterThread() {
        new Thread(new FindPeersBroadcasterRunnable()).start();
    }

    private void startFindPeerMessageNotifierThread() {
        new Thread(new FindPeerMessageNotifierRunnable()).start();
    }

    private void startSendPeerMessageNotifierThread() {
        new Thread(new SendPeerMessageNotifierRunnable()).start();
    }

    private void startServerSocketThread() {
        new Thread(new ServerSocketRunnable(serverSocket)).start();
    }

    private void startDecoderThread() {
        new Thread(new DecoderRunnable()).start();
    }

    private void startInboundExecutorPoolThread() {
        new Thread(new InboundExecutorRunnable()).start();
    }

    private void startOutboundExecutorPoolThread() {
        new Thread(new OutboundExecutorRunnable()).start();
    }

    void sendTextMessage(char identifier, String payload) {
        ioData.insertOutboundMessage(identifier,
                networkData.getSelfIP(),
                networkData.getSelfPORT(),
                networkData.getSelfAlias(),
                payload.getBytes()
        );
    }


}
