package com.cups.splashin.peer2party.networker.server;


import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

public class FindPeerMessageNotifierRunnable extends PeerMessageHandler implements Runnable {

    private final static short MAX_PEER_NOTIFY_LENGTH = 256;

    private final DatagramPacket receivePacket;
    private final IODataSingleton ioData = IODataSingleton.getInstance();
    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();

    public FindPeerMessageNotifierRunnable(){
        super(StaticHelper.getUdpDeclareMsgPort());

        receivePacket = new DatagramPacket(new byte[MAX_PEER_NOTIFY_LENGTH], MAX_PEER_NOTIFY_LENGTH);
    }

    @Override
    public void run() {
        while(true){
            receivePackets(getUdpSocket(), receivePacket);
        }
    }

    @Override
    void handleReceivedPacket(String[] receivedData) {
        if (!(receivedData.length == 5))
            return;

        String ip = receivedData[1];
        int port = Integer.parseInt(receivedData[2]);
        String alias = receivedData[3];

        boolean selfCheck  = networkData.ifPeerIsSelf(ip,port,alias);

        if (selfCheck) {
            return;
        }

        boolean peerExists = networkData.ifPeerExists(ip, port, alias);
        boolean msg_check  = receivedData[4].equals("MSG");

        if (peerExists && msg_check && !selfCheck) {
            System.out.println("Broadcast MSG Thread: MSG available from an IP & port...");
            networkData.insertPeerMessenger(new Peer(ip, port, alias));
            return;
        }

        if (msg_check && !selfCheck) {
            try {
                Socket s = new Socket(ip,port);
                sendAlias(ip, port, networkData.getSelfAlias(), s);

                Peer p = new Peer(ip,port,alias,s);

                networkData.insertPeer(p);
                networkData.insertPeerMessenger(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
