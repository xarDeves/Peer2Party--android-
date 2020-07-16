package com.cups.splashin.peer2party.networker.server;

import java.net.DatagramSocket;
import java.net.SocketException;

abstract class PeerMessageHandler extends Broadcaster {

    private static DatagramSocket udpSocket = null;

    PeerMessageHandler(int port) {
        try {
            if (udpSocket == null){
                udpSocket = new DatagramSocket(port);
                udpSocket.setBroadcast(true);
            }
        } catch (SocketException e){
            e.printStackTrace();
        }
    }

    public static DatagramSocket getUdpSocket() {
        return udpSocket;
    }
}
