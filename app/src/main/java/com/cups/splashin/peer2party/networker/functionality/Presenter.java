package com.cups.splashin.peer2party.networker.functionality;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonIOData;
import com.cups.splashin.peer2party.networker.networking.singleton.SingletonNetworkData;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class Presenter {
    private Model model;

    private SingletonNetworkData networkData;
    private SingletonIOData ioData;

    private LinkedList<String> peerList;

    public Presenter(String username, Object swingWorkerLock) {
        this.model = new Model(username, swingWorkerLock);
        this.model.startNetworking();

        peerList = new LinkedList<>();

        networkData = SingletonNetworkData.getInstance();
        ioData = SingletonIOData.getInstance();
    }

    public void sendMessage(char type, @NotNull String s) {
        model.sendMessage((byte) type, s.getBytes());
        ioData.insertMessageProcessed(s, networkData.getALIAS(), null, 't');
    }

    public LinkedList<String> getPeerNamesAndPortsPanels() {
        String[][] peerData = model.getAllNodeAliasPort();

        if (peerData == null) {
            return null;
        }

        Arrays.sort(peerData, new Comparator<String[]>() {
            @Override
            public int compare(final String[] s1, final String[] s2) {
                return s2[0].compareTo(s1[0]);
            }
        });

        LinkedList<String> newPeers = new LinkedList<>();

        for (String[] strArr : peerData) {
            if (!peerList.contains(strArr[0]) || !peerList.contains(strArr[1])) {
                peerList.add(strArr[0]);
                peerList.add(strArr[1]);

                newPeers.add(strArr[0]);
                newPeers.add(strArr[1]);
            }
        }

        return newPeers;
    }

    public void enableCommunicationWithUser(String alias, String port) {
        System.out.println("Main thread: Presenter: Adding user " + alias + port);
        //should add him to a inputoutput communication list
    }

    public void disableCommunicationWithUser(String alias, String port) {
        System.out.println("Main thread: Presenter: Removing user " + alias + port);
        //should remove him to a inputoutput communication list
    }
}
