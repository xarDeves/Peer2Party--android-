package com.cups.splashin.peer2party.networker.server;

import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class FindPeersBroadcasterRunnable extends Broadcaster implements Runnable {

    private DatagramSocket broadcastSocket;
    private DatagramPacket receivePacket;

    private byte[] receiveData;

    private final IODataSingleton ioData = IODataSingleton.getInstance();
    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();

    public FindPeersBroadcasterRunnable() {
        try {
            broadcastSocket = new DatagramSocket(StaticHelper.getUdpDeclarePeersPort());
            broadcastSocket.setBroadcast(true);
            receiveData = new byte[256];
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void run() {
        int i = 0;
        while (true) {
            if ((i % 30) == 0) {
                for (int j = 0; j < 3; j++) {
                    try{
                        broadcastStr(broadcastSocket,
                                StaticHelper.getSoftwareIdentifier() + ","
                                + networkData.getSelfIP() + ","
                                + networkData.getTcpPort() + ","
                                + networkData.getSelfAlias(),
                                StaticHelper.getUdpDeclarePeersPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            receivePackets(broadcastSocket, receivePacket);
            i++;
        }
    }

    @Override
    void handleReceivedPacket(String[] receivedData) {
        if (!(receivedData.length == 4))
            return;

        String ip = receivedData[1];
        int port = Integer.parseInt(receivedData[2]);
        String alias = receivedData[3];

        if (networkData.ifPeerIsSelf(ip,port,alias))
            return;

        boolean peerCheck = networkData.ifPeerExists(ip, port, alias);

        if (!peerCheck) {
            System.out.println("Broadcast Thread: Appended to list & active sockets!");

            try {
                Socket s = new Socket(ip, port);                         //sends alias
                sendAlias(ip, port, networkData.getSelfAlias(), s);

                networkData.insertPeer(new Peer(ip, port, alias, s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
