package com.cups.splashin.peer2party.networker.networking.singleton;

import android.util.Log;

import com.cups.splashin.peer2party.PeerTransaction;

import org.greenrobot.eventbus.EventBus;

import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;

public class SingletonNetworkData {

    private static SingletonNetworkData instance = null;

    private static IPPortList ipPortList;

    private static NodeDataList nodeDataList;

    private static SelfData selfData;

    private static SocketList socketList;

    private final Object DataAppendLock;

    private SingletonNetworkData() {
        DataAppendLock = new Object();
    }

    public static SingletonNetworkData getInstance() {
        if (instance == null) {
            throw new ExceptionInInitializerError("Self data not initialized. First use other constructor.");
        }
        return instance;
    }

    public static SingletonNetworkData getInstance(String ip, int port, String alias) {
        if (instance == null) {
            instance = new SingletonNetworkData();

            initIpPortList();
            initNodeDataList();
            initSelfData(ip, port, alias);
            initSocketList();


        }
        return instance;
    }

    private static void initIpPortList() {
        ipPortList = new IPPortList();
        ipPortList.ipPortQueue = new LinkedList<>();
    }

    private static void initNodeDataList() {
        nodeDataList = new NodeDataList();
        nodeDataList.nodeData = new LinkedList<>();
    }

    private static void initSelfData(String ip, int port, String alias) {
        selfData = new SelfData(ip, port, alias);
    }

    private static void initSocketList() {
        socketList = new SocketList();
        socketList.socketList = Collections.synchronizedList(new LinkedList<>());
    }

    public String[] getElementIPPortList() {
        return ipPortList.getLastElement();
    }

    public String[] popElementIPPortList() {
        return ipPortList.popLastElement();
    }

    public void pushElementIPPortList(String ip, Integer port) {
        ipPortList.pushElement(ip, port);
    }

    public boolean hasElementsIPPortList() {
        return ipPortList.hasElements();
    }

    public void appendNodeData(String ip, Integer p, String alias) {
        nodeDataList.appendNodeData(ip, p, alias);
    }

    public int getIndexOfIPPort(String ip, Integer p) {
        return nodeDataList.getIndexOfIPPort(ip, p);
    }

    public void removeIpPort(String ip, Integer p) {
        nodeDataList.removeIpPort(ip, p);
    }

    public boolean containsIP(String ip) {
        return nodeDataList.containsIP(ip);
    }

    public boolean containsPORT(Integer p) {
        return nodeDataList.containsPORT(p);
    }

    public boolean containsIPPORT(String ip, Integer p) {
        return nodeDataList.getIndexOfIPPort(ip, p) != -1;
    }

    public String[][] getAllNodeAliasPort() {
        return nodeDataList.getAllNodeAliasPort();
    }

    public Integer getUdpDeclareMsgPort() {
        return selfData.getUdpDeclareMsgPort();
    }

    public Integer getUdpDeclarePeersPort() {
        return selfData.getUdpDeclarePeersPort();
    }

    public String getSoftwareIdentifier() {
        return selfData.getSoftwareIdentifier();
    }

    public Integer getTcpPort() {
        return selfData.getTcpPort();
    }

    public String getIP() {
        return selfData.getIP();
    }

    public String getALIAS() {
        return selfData.getALIAS();
    }

    public void addSocket(Socket s) {
        socketList.addElement(s);
    }

    public void removeSocket(Socket s) {
        socketList.removeSocket(s);
    }

    public int getSizeSocketList() {
        return socketList.getSize();
    }

    public int getSocketIndexFromIPPort(String s, int p) {
        return nodeDataList.getIndexOfIPPort(s, p) - 1;
    }

    public Socket getSocket(int i) {
        return socketList.getSocket(i);
    }

    public void insertIPPORTALIASSocket(String ip, int port, String alias, Socket s) {
        synchronized (DataAppendLock) {
            nodeDataList.appendNodeData(ip, port, alias);
            socketList.addElement(s);
            ipPortList.pushElement(ip, port);
            PeerTransaction event = new PeerTransaction();
            Log.d("fuck", "SingletonNetworkData ok");
            EventBus.getDefault().postSticky(event);
        }
    }

}
