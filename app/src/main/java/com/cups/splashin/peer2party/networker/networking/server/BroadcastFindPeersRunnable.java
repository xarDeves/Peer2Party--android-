package com.cups.splashin.peer2party.networker.networking.server;

import android.util.Log;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonIOData;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class BroadcastFindPeersRunnable extends Broadcaster implements Runnable {

    private DatagramSocket broadcastSocket;
    private DatagramPacket receivePacket;

    private byte[] receiveData;

    private SingletonIOData ioData = SingletonIOData.getInstance();
    private SingletonNetworkData networkData = SingletonNetworkData.getInstance();

    public BroadcastFindPeersRunnable() {
        try {
            broadcastSocket = new DatagramSocket(networkData.getUdpDeclarePeersPort());
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
                i = 0;
                for (int j = 0; j < 3; j++) {
                    try {
                        broadcastStr(broadcastSocket,
                                networkData.getSoftwareIdentifier() + ","
                                        + networkData.getIP() + ","
                                        + networkData.getTcpPort() + ","
                                        + networkData.getALIAS(),
                                networkData.getUdpDeclarePeersPort());
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
        String alias = receivedData[3];
        int port = Integer.parseInt(receivedData[2]);
        boolean ip_check = networkData.containsIP(ip);
        boolean port_check = networkData.containsPORT(port);

        if (!ip_check || !port_check) {
            Log.d("networker","Broadcast Thread: Appended to list & active sockets!");

            try {
                Socket s = new Socket(ip, port);                         //sends alias
                PrintWriter pr = new PrintWriter(s.getOutputStream());  //sends alias

                Log.d("networker","Broadcast Thread: Sending alias");

                s.setSoTimeout(1000);
                pr.println(networkData.getALIAS());                        //sends alias
                s.setSoTimeout(0);
                networkData.insertIPPORTALIASSocket(ip, port, alias, s);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
