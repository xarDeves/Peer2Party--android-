package com.cups.splashin.peer2party.networker.helper;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class StaticHelper {

    private static ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);

    private StaticHelper() {}

    public static byte[] convertLongToByteArray(@NotNull Long i){
        byteBuffer.putLong(0, i);
        return byteBuffer.array();
    }

    public static String convertToLegalName(@NotNull String s){
        return s.trim().replace(",", "").replace(" ","");
    }
}
