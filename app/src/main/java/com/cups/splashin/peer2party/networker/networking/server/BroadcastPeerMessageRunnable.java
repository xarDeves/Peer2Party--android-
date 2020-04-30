package com.cups.splashin.peer2party.networker.networking.server;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class BroadcastPeerMessageRunnable extends Broadcaster implements Runnable {

    private final Object lock;

    private DatagramPacket receivePacket;
    private DatagramSocket udpSocket;
    private SingletonNetworkData networkData;

    public BroadcastPeerMessageRunnable(Object lock) {
        this.lock  = lock;

        byte[] receiveData = new byte[256];
        networkData = SingletonNetworkData.getInstance();
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
    }

    @Override
    public void run() {
        System.out.println("Broadcast peer thread:Starting peer msg thread...");
        while (true) {
            udpSocket = createSocket(networkData.getUdpDeclareMsgPort());

            if (!udpSocket.isClosed()) {
                System.out.println("Broadcast peer thread: Receiving msg available packets...");

                synchronized (lock) {
                    receivePackets(udpSocket, receivePacket);
                    Broadcaster.closePeerMsgSocket();

                    lock.notify();
                }
            }

            if (udpSocket.isClosed()) {
                System.out.println("Broadcast peer thread: notifying there's a message on our side...");

                udpSocket = createSocket(networkData.getUdpDeclareMsgPort());

                try {
                    //Note to reader:
                    // it's possible this will broadcast that the payload is sent BEFORE it's actually sent to all nodes
                    // in the network: I have tried to wait() and notify, but it results in a deadlock which I haven't
                    // fixed yet.
                    broadcastStr(udpSocket,
                            networkData.getIP() + ","
                            + networkData.getTcpPort() + ","
                            + networkData.getALIAS() + ",MSG",
                            networkData.getUdpDeclareMsgPort());

                    Broadcaster.closePeerMsgSocket();
                }catch (IOException ex){
                    System.out.println("Broadcast peer thread: Got socketexception");
                }
            }
        }
    }

    @Override
    void handleReceivedPacket(String[] receivedData) {
        if (!(receivedData.length == 5))
            return;

        String ip = receivedData[1];
        String alias = receivedData[3];
        int port = Integer.parseInt(receivedData[2]);

        boolean ip_check   = !ip.equals(networkData.getIP()) && networkData.containsIP(ip);
        boolean port_check = !networkData.getTcpPort().equals(port) && networkData.containsPORT(port);
        boolean msg_check  = receivedData[3].equals("MSG");

        boolean ip404   = !ip_check && !ip.equals(networkData.getIP());
        boolean port404 = !port_check && !networkData.getTcpPort().equals(port);

        if (ip_check && port_check && msg_check) {
            System.out.println("Broadcast MSG Thread: MSG available from an IP & port.. closing socket & pushing");
            networkData.pushElementIPPortList(ip, port);
        }else if((ip404 || port404) && msg_check){
            try{
                Socket s = new Socket(ip,port);
                sendAlias(ip, port, networkData.getALIAS(), s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
