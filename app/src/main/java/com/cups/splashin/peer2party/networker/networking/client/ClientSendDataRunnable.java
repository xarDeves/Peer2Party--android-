package com.cups.splashin.peer2party.networker.networking.client;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSendDataRunnable implements Runnable {

    private Byte identifier;
    private Byte[]   length;
    private Byte[]  payload;

    private Socket socket;
    private DataOutputStream out;
    private SingletonNetworkData networkData;

    public ClientSendDataRunnable(Socket s, Byte[] identif, Byte[] len, Byte[] payload) {
        socket = s;

        identifier = identif[0];
        length = len;
        this.payload = payload;

        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        //byte identifier = this.identifier;
        byte[] length   = new byte[this.length.length];
        byte[] payload  = new byte[this.payload.length];

        int i = 0;
        for(Byte b : this.payload){
            payload[i++] = b;
        }

        int j = 0;
        for(Byte b : this.length){
            length[j++] = b;
        }

        //note to reader: this function will not send above 10k bytes.
        //this should be fixed to loop correctly
        try{
            out.write(payload, 0, this.payload.length);
            out.flush();
            System.out.println("Client out connection thread: sent bytes");
        }catch (IOException ex){
            networkData = SingletonNetworkData.getInstance();

            networkData.removeIpPort(socket.getInetAddress().toString(), socket.getPort());
            networkData.removeSocket(socket);

            System.out.println("Got IOException from socket. Removed from everywhere..!");
        }
    }
}
