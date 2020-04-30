package com.cups.splashin.peer2party.networker.networking.server;


import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.IllegalCharsetNameException;

public class ServerSocketRunnable implements Runnable {
    private ServerSocket socket;

    private SingletonNetworkData networkData = SingletonNetworkData.getInstance();

    public ServerSocketRunnable(ServerSocket s) {
        socket = s;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("ServerSocket Thread: Waiting for somebody... So lonely...");
                Socket s = socket.accept();

                /*if the IP & port already exist within the list,
                 * close the socket,
                 *else
                 * append it to the list */

                if (networkData.containsIP(s.getInetAddress().toString())
                        && networkData.containsPORT(s.getPort())) {
                    System.out.println("ServerSocket Thread: Found duplicate, closing connection...");
                    socket.close();

                } else {
                    System.out.println("ServerSocket Thread: User connected! Appending to list!"); // for debug only

                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    s.setSoTimeout(1000);
                    String alias = br.readLine();
                    s.setSoTimeout(0);
                    alias = StaticHelper.convertToLegalName(alias);
                    if (alias.equals("")){
                        throw new IllegalCharsetNameException("Illegal name");
                    }

                    networkData.insertIPPORTALIASSocket(s.getInetAddress().toString(), s.getPort(), alias, s);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
