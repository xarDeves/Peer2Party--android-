package com.cups.splashin.peer2party.networker.networking.server;


import android.util.Log;

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
                Log.d("networker","ServerSocket Thread: Waiting for somebody... So lonely...");
                Socket s = socket.accept();

                /*if the IP & port already exist within the list,
                 * close the socket,
                 *else
                 * append it to the list */

                if (networkData.containsIP(s.getInetAddress().toString())
                        && networkData.containsPORT(s.getPort())) {
                    Log.d("networker","ServerSocket Thread: Found duplicate, closing connection...");
                    s.close();

                } else {
                    Log.d("networker", "ServerSocket Thread: User connected! Appending to list!"); // for debug only

                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    s.setSoTimeout(2000);
                    String alias = br.readLine();
                    s.setSoTimeout(0);
                    alias = StaticHelper.convertToLegalName(alias);
                    Log.d("networker", "ServerSocket Thread: got alias " + alias);

                    if (alias.equals("")) {
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
