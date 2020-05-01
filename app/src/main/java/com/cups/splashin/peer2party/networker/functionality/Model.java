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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

//TODO other than refactoring to kotlin, refactor the file tree as a final step for v1.0 completion
public class Model {

    private ServerSocket serverSocket;

    private SingletonNetworkData networkData;
    private SingletonIOData ioData;

    private final Object findPeersLock = new Object();
    private final Object decoderLock = new Object();
    private String ipAddress;

    LinkedList<String> peerList;

    public Model(String username, String ipAddress) {
        this.ipAddress = ipAddress;
        this.peerList = new LinkedList<>();
        this.initializeServerVariables(username);
        this.startNetworking();
    }

    void startNetworking() {
        startDecoderThread();
        startServerSocketThread();
        startExecutorPoolThread();
        startBroadcastingFindMsgrThread();
        startBroadcastingFindPeersThread();
    }

    public void sendMessage(char type, @NotNull String s) {
        sendMessage((byte) type, s.getBytes());
        ioData.insertMessageProcessed(s, networkData.getALIAS(), null, 't');
    }

    public LinkedList<String> getPeerNamesAndPortsPanels() {
        String[][] peerData = networkData.getAllNodeAliasPort();

        if (peerData == null) {
            return null;
        }

        Arrays.sort(peerData, new Comparator<String[]>() {
            @Override
            public int compare(final String[] s1, final String[] s2) {
                return s2[0].compareTo(s1[0]);
            }
        });

        //TODO modify this guy...every time i rotate the screen "newPeers" is empty, why not return peerList?
        //TODO also...if you could make this a 2D List you've got a free blowjob <3 (tho on kotlin refactoring will be obsolete)


        LinkedList<String> newPeers = new LinkedList<>();

        for (String[] strArr : peerData) {
            if (!peerList.contains(strArr[0]) || !peerList.contains(strArr[1])) {
                peerList.add(strArr[0]);
                peerList.add(strArr[1]);

                newPeers.add(strArr[0]);
                newPeers.add(strArr[1]);
            }
        }

        return peerList;
    }

    public void enableCommunicationWithUser(String alias, String port) {
        System.out.println("Main thread: Model: Adding user " + alias + port);
        //should add him to a inputoutput communication list
    }

    public void disableCommunicationWithUser(String alias, String port) {
        System.out.println("Main thread: Model: Removing user " + alias + port);
        //should remove him to a inputoutput communication list
    }


    private void initializeServerVariables(String username) {
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

            networkData.appendNodeData(networkData.getIP(), networkData.getTcpPort(), username);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private String getTrueOutputAddress() {
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
        new Thread(new DecoderRunnable(decoderLock)).start();
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

}
