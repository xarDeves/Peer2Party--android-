package com.cups.splashin.peer2party.networker.networking.codec;

import android.util.Log;

import com.cups.splashin.peer2party.networker.networking.singleton.SingletonIOData;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

public class DecoderRunnable implements Runnable {

    private SingletonIOData ioData;

    private final Object decoderLock;

    private String saveDirectory = "c:/users/epics/desktop"; // change later to dynamic
    SimpleDateFormat dateFormat;

    public DecoderRunnable(Object decoderLock) {
        ioData = SingletonIOData.getInstance();
        dateFormat = new SimpleDateFormat("MMddyyyy_hhmmss", Locale.getDefault());

        this.decoderLock = decoderLock;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (decoderLock) {
                while (!ioData.hasElementsInboundData()) {
                    try {
                        Log.d("networker", "Decoder thread: Waiting for inboundData to have elements...");
                        decoderLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            Log.d("networker", "Decoder thread: Starting decoding");
            decode();
        }
    }

    private void decode() {
        LinkedList<String> pastkeys = new LinkedList<>();
        LinkedList<String> workKeys = new LinkedList<>();
        LinkedList<Long> strDeinc = new LinkedList<>();
        //when we wake up and we have some elements in the inboundData map...
        // all of thisneeds rewriting to be more efficient
        while (ioData.hasElementsInboundData()) {
            Enumeration<String> keys = ioData.getKeysInboundData();

            //maybe join all these loops together
            for (String s = keys.nextElement(); keys.hasMoreElements(); s = keys.nextElement()) {
                pastkeys.add(s);
                strDeinc.add(Long.parseLong(s.substring(17, 35)));
            }

            for (int i = 0; i < pastkeys.size(); i++) {
                String key = pastkeys.get(i);
                String keyidentifier = key.substring(36, 39);

                //this shouldn't be like this: maybe it should be added to a special list for incoming BIG data,
                // and maybe spawn a thread to handle specifically each one
                if (Integer.parseInt(key.substring(17, 35)) > pastkeys.size() + 10) {
                    //maybe spawn thread to handle this
                }

                for (int j = 0; j < pastkeys.size(); j++) {
                    // if identifier equals
                    if (keyidentifier.equals(pastkeys.get(j).substring(36, 39))) {
                        long newnum = strDeinc.get(i) - 1;
                        strDeinc.add(i, newnum);

                        if (newnum == 0) {
                            workKeys.add(key);
                            break;
                        }
                    }
                }
            }

            for (String key : workKeys) {
                decodeData(key);
            }

            // clear all keys at the end so we can start w/ fresh new ones
            for (String key : pastkeys) {
                decodeData(key);
                ioData.removeElementInboundData(key);
            }

            pastkeys.clear();
            strDeinc.clear();
            workKeys.clear();

        }
    }

    private void decodeData(@NotNull String key) {
        Log.d("networker", "Decoder thread: decoding " + key);
        switch (key.charAt(35)) {
            case ('t'):
                decodeText(key);
                break;
            case ('p'):
            case ('v'):
                String name = saveFile(key);
                createThumbnail(key.charAt(35), name);
                break;
        }
    }

    private void decodeText(@NotNull String key) {
        long nodenumber = Long.parseLong(key.substring(17, 35));
        String nodeidentifier = key.substring(17, 39);

        StringBuilder t = new StringBuilder();
        for (long i = 0; i <= nodenumber; i++) {
            t.append(new String(ioData.getElementInboundData(getKeyInMap(i, nodeidentifier)), StandardCharsets.UTF_8));
        }

        ioData.insertMessageProcessed(t.toString(), "Placeholder", null, 't'); // change later
        Log.d("fuck", "Decoder thread: got text" + t);
    }


    private String saveFile(@NotNull String key) {
        long nodenumber = Long.parseLong(key.substring(17, 35));
        String nodeidentifier = key.substring(17, 39);
        FileOutputStream fileout = null;

        String currentTime = dateFormat.format(System.currentTimeMillis());
        String extension = ".png";
        try {
            fileout = new FileOutputStream(currentTime + extension); //change the extension to be dynamic
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {

            for (long i = 0; i <= nodenumber; i++) {
                byte[] data = ioData.getElementInboundData(getKeyInMap(i, nodeidentifier));

                Objects.requireNonNull(fileout).write(data);
                fileout.flush();
            }

            Objects.requireNonNull(fileout).close();

            return currentTime + extension; //not for use yet
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //TODO implement android logic (deprecated)
    private void createThumbnail(char c, @NotNull String filename) {

    }

    private String getKeyInMap(long i, String nodeidentifier) {
        return String.format("%016d", i) + nodeidentifier;
    }
}
