package com.cups.splashin.peer2party.networker.client;


import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ClientReceiveDataRunnable implements Runnable {

    private static final short MAX_PACKET_SIZE = 4096;

    private final Peer peer;

    private DataInputStream in;

    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();

    public ClientReceiveDataRunnable(Peer p) {
        peer  = p;

        try {
            in = new DataInputStream(peer.getSocket().getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            char identifier = (char) in.readByte();
            int length      = in.readInt();

            System.out.println("Client receive connection thread: got identifier " + identifier);
            System.out.println("Client receive connection thread: got length " + length);

            int len;
            if (length < MAX_PACKET_SIZE){
                len = length;
            } else {
                len = MAX_PACKET_SIZE;
            }

            ArrayList<byte[]> data = new ArrayList<>();
            byte[] dataBuffer = new byte[len];

            int currentbytesread = 0;
            int totalbytesread = 0;

            while(currentbytesread != -1 && currentbytesread != length){
                currentbytesread = in.read(dataBuffer);
                totalbytesread  += currentbytesread;

                if(currentbytesread != -1){
                    data.add(dataBuffer.clone());
                }
            }

            System.out.println("Client in connection thread: stopped reading because " +currentbytesread + " " + (currentbytesread != length) );

            IODataSingleton ioData = IODataSingleton.getInstance();
            ioData.insertUnprocessedMessage(identifier, peer.getIP(), peer.getPORT(), peer.getALIAS(), data, totalbytesread);

            System.out.println("Client in connection thread: Done reading from peer "
                    + peer.getIP() + peer.getPORT() + peer.getALIAS()
            );

        } catch (IOException ex) {
            networkData.removePeer(peer);
            System.out.println("Client in connection thread: Got IOException. Removed peer!\n");
            ex.printStackTrace();
        }

    }
}
