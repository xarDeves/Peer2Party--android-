package com.cups.splashin.peer2party.networker.codec;


import android.media.Image;

import com.cups.splashin.peer2party.networker.singleton.IODataSingleton;
import com.cups.splashin.peer2party.networker.singleton.data.MessageBundle;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

public class DecoderRunnable implements Runnable {

    private final IODataSingleton ioData = IODataSingleton.getInstance();

    private final Object decoderLock = ioData.getUnprocessedQueueLock();

    private final String saveDirectory = "C:\\Users\\epics\\Desktop"; // change later to dynamic

    SimpleDateFormat dateFormat;

    public DecoderRunnable(){
        dateFormat = new SimpleDateFormat("MMddyyyy_hhmmss", Locale.getDefault());
    }

    @Override
    public void run() {
        while (true){
            synchronized (decoderLock){
                while(ioData.isEmptyUnprocessedQueue()){
                    try {
                        System.out.println("Decoder thread: Waiting for inboundData to have elements...");
                        decoderLock.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Decoder thread: Woke up!");
                    }
                }
            }

            System.out.println("Decoder thread: Starting decoding");
            decode();
        }
    }

    private void decode(){
        while(!ioData.isEmptyUnprocessedQueue()){
            MessageBundle bundle = ioData.getUnprocessedMessage();

            if (bundle.getIdentifier() == 'p' || bundle.getIdentifier() == 'v')
                saveDataToDisk(bundle);

            ioData.insertProcessedMessage(bundle.getIdentifier(), bundle.getIp(), bundle.getPort(), bundle.getAlias(),
                    getTextFromBundle(bundle), getThumbnailFromBundle(bundle));
        }
    }

    private String getTextFromBundle(MessageBundle b){
        if (b.getIdentifier() == 't'){
            return new String(b.getData());
        }
        return "";
    }

    private LinkedList<Image> getThumbnailFromBundle(MessageBundle b){
        //if (b.getIdentifier() == 'f' || b.getIdentifier() == 'v')
        return null;
    }

    private void saveDataToDisk(MessageBundle b){


    }

}
