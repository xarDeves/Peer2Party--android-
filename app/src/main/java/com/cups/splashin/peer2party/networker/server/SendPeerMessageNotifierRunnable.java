package com.cups.splashin.peer2party.networker.server;


import com.cups.splashin.peer2party.networker.helper.StaticHelper;
import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.NetworkDataSingleton;

import java.io.IOException;

public class SendPeerMessageNotifierRunnable extends PeerMessageHandler implements Runnable {

    private final NetworkDataSingleton networkData = NetworkDataSingleton.getInstance();
    private final IODataSingleton ioData = IODataSingleton.getInstance();

    private final Object outboundQueueLock = ioData.getOutboundQueueLock();

    public SendPeerMessageNotifierRunnable(){
        super(StaticHelper.getUdpDeclareMsgPort());
    }

    @Override
    public void run() {
        boolean sentNotification = false;

        while(true){
            synchronized (outboundQueueLock) {
                while (ioData.isEmptyOutboundQueue() || !sentNotification) {
                    try {
                        outboundQueueLock.wait();
                        sentNotification = true;
                    } catch (InterruptedException e) {
                        System.out.println("Send Peer Message Notifier: Interrupted!");
                    }

                }

                try {
                    broadcastStr(getUdpSocket(),
                            StaticHelper.getSoftwareIdentifier() + ","
                                    + networkData.getSelfIP() + ","
                                    + networkData.getTcpPort() + ","
                                    + networkData.getSelfAlias() + ",MSG",
                            StaticHelper.getUdpDeclareMsgPort());
                    sentNotification = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Send Peer Notifier Thread: sent notification!" + ioData.isEmptyOutboundQueue() + !sentNotification);
            }
        }
    }

    @Override
    void handleReceivedPacket(String[] receivedData) {}
}
