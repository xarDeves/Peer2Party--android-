package com.cups.splashin.peer2party.networker.server;


import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;
import com.cups.splashin.peer2party.networker.throwables.IllegalAliasException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketRunnable implements Runnable {
    private final ServerSocket socket;

    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();

    public ServerSocketRunnable(ServerSocket s) {
        socket = s;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("ServerSocket Thread: Waiting for somebody... So lonely...");
                Socket s = socket.accept();

                /* if the IP & port already exist within the list,
                 * close the socket,
                 * else
                 * append it to the list */
                System.out.println("ServerSocket Thread: User connected! Appending to list!"); // for debug only
                String alias = validateSocket(s);
                boolean peerCheck = networkData.ifPeerExists(s.getInetAddress().toString(), s.getPort(), alias);
                boolean selfCheck = networkData.ifPeerIsSelf(s.getInetAddress().toString(), s.getPort(), alias);

                if (!peerCheck && !selfCheck)
                    networkData.insertPeer(s.getInetAddress().toString(), s.getPort(), alias, s);

            } catch (IOException | IllegalAliasException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String validateSocket(Socket s) throws IOException, IllegalAliasException {
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        s.setSoTimeout(2000);
        String alias = br.readLine();
        s.setSoTimeout(0);
        System.out.println("ServerSocket Thread: Got Alias " + alias);
        alias = StaticHelper.convertToLegalName(alias);

        if (alias.equals("")){
            throw new IllegalAliasException("Illegal name");
        }

        return alias;
    }
}
