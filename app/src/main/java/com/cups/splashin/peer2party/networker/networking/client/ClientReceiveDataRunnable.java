package com.cups.splashin.peer2party.networker.networking.client;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonIOData;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientReceiveDataRunnable implements Runnable {

    private Socket socket;
    private DataInputStream in;

    private SingletonIOData ioData;

    private Random rand;

    private SingletonNetworkData networkData;

    public ClientReceiveDataRunnable(Socket s) {
        socket = s;
        ioData = SingletonIOData.getInstance();
        rand = new Random();

        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            final short MAX_PACKET_SIZE = 4096;

            char identifier = in.readChar();
            long length     = in.readLong();
            System.out.println("Client in connection thread: got " + identifier);
            System.out.println("Client in connection thread: got " + length);

            int len;
            if (length < MAX_PACKET_SIZE){
                len = (int) length;
            }else{
                len = MAX_PACKET_SIZE;
            }

            byte[] dataBuffer = new byte[len];


            // might have problems: lets say there's a 0.01 result: it'll go down to 0. should fix later
            String numberofnodes  = String.format("%016d", ((int) Math.ceil(len/MAX_PACKET_SIZE)));
            String nodeidentifier = String.format("%03d", rand.nextInt(1000));

            String postfix = numberofnodes + identifier + nodeidentifier;

            System.out.println("Client in connection thread: postfix is " + postfix);

            long currentbytesread;
            long totalbytesread = 0;

            boolean reading  = true;
            int index = 0;

            while(reading){
                currentbytesread = in.read(dataBuffer);
                totalbytesread  += currentbytesread;

                ioData.insertElementInboundData(String.format("%016d", index++) + postfix, dataBuffer);

                if(totalbytesread >= length || currentbytesread == -1){
                    reading = false;
                }
            }
            System.out.println("Client in connection thread: Done reading");
        } catch (IOException ex) {
            networkData = SingletonNetworkData.getInstance();

            networkData.removeIpPort(socket.getInetAddress().toString(), socket.getPort());
            networkData.removeSocket(socket);

            System.out.println("Client in connection thread: Got IOException. Removed from everywhere!");
        }
    }
}
