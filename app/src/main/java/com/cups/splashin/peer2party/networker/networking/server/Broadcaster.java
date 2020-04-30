package com.cups.splashin.peer2party.networker.networking.server;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

abstract public class Broadcaster {

    private SingletonNetworkData networkData = SingletonNetworkData.getInstance();

    private static volatile DatagramSocket peerMsgSocket;

    void receivePackets(DatagramSocket udpsocket, DatagramPacket receivePacket) {
        try {
            System.out.println("Broadcast Thread: Waiting to receive UDP packets...");
            udpsocket.receive(receivePacket);
        } catch (IOException ex) {
            System.out.println("Broadcast Thread: Socket Closed. Returning...");
            return;
        }

        String[] broadcastStringReceived = new String(
                receivePacket.getData(),
                0,
                receivePacket.getLength()).split(",");

        handleReceivedPacket(broadcastStringReceived);
    }

    abstract void handleReceivedPacket(String[] receivedData);

    void broadcastStr(DatagramSocket udpsocket, String s, int port) throws IOException {
        broadcast(udpsocket, s, InetAddress.getByName("255.255.255.255"), port);
    }

    private void broadcast(DatagramSocket udpsocket, String s, InetAddress address, int port) throws IOException {
        byte[] buff = s.getBytes();

        udpsocket.send(new DatagramPacket(buff, buff.length, address, port));
        System.out.println("Broadcast Thread: Sent DatagramPackets! " + s);
    }

    void sendAlias(String ip, int port, String alias, Socket s) throws IOException {
        PrintWriter pr = new PrintWriter(s.getOutputStream());  //sends alias

        s.setSoTimeout(1000);
        System.out.println("Broadcaster Thread: Sending alias");
        pr.println(alias);                     //sends alias
        s.setSoTimeout(0);

        System.out.println("Broadcaster Thread: Appended to list & active sockets!");
        networkData.insertIPPORTALIASSocket(ip, port, alias, s);
    }

    DatagramSocket createSocket(int port) {
        DatagramSocket udpSocket;
        try {
            udpSocket = new DatagramSocket(port);
            udpSocket.setBroadcast(true);
            if (port == networkData.getUdpDeclareMsgPort()) {

                if (peerMsgSocket == null) {
                    peerMsgSocket = udpSocket;
                } else {
                    synchronized (peerMsgSocket) {
                        peerMsgSocket = udpSocket;
                    }
                }

            }


            return udpSocket;
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void closePeerMsgSocket() {
        peerMsgSocket.close();
    }
}
