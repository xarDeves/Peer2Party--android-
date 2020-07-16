package com.cups.splashin.peer2party.networker.client;


import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.MessageBundle;
import com.cups.splashin.peer2party.networker.singleton.data.Peer;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClientSendDataRunnable implements Runnable {

    private final MessageBundle bundle;

    private final Peer peer;
    private DataOutputStream out;

    public ClientSendDataRunnable(Peer p, MessageBundle b) {
        peer = p;
        bundle = b;

        try {
            out = new DataOutputStream(p.getSocket().getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {

        byte[] i = new byte[] {(byte) bundle.getIdentifier()};
        byte[] s = StaticHelper.convertIntToByteArray(bundle.getSize());
        byte[] p = bundle.getData();

        //note to reader: this function might not be able to send above 10k bytes.
        //might be needed to be fixed to loop
        try{
            out.write(i, 0, i.length);
            out.write(s, 0, s.length);
            out.write(p, 0, p.length);
            out.flush();
            System.out.println("Client out connection thread: sent bytes");
        }catch (IOException ex){
            NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();
            networkData.removePeer(peer);

            System.out.println("Client out connection thread: Got IOException from socket. Removed peer! ");
            ex.printStackTrace();
        }
    }
}
