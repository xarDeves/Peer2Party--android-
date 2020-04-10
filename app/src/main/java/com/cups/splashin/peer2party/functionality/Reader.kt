package com.cups.splashin.peer2party.functionality

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.cups.splashin.peer2party.data.MessageDataClass
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object Reader {

    private var streamReader: InputStreamReader? = null
    private var message: String = ""
    private lateinit var br: BufferedReader

    fun readText(
        messageByte: ByteArray,
        inputStream: InputStream
    ): String {
        streamReader = InputStreamReader(inputStream)
        br = BufferedReader(
            streamReader
        )
        message = br.readText()
        message = String(messageByte, charset("UTF-8"))
        return message
    }


    fun readImage(
        messageByteClean: ByteArray,
        screenW: Int,
        screenH: Int,
        imageWidth: Int,
        imageHeight: Int
    ): Bitmap {
        //attempt to decode the image's width and height in order to downscale for thumbnail creation:

        val imageRaw = BitmapFactory.decodeByteArray(messageByteClean, 0, messageByteClean.size)
        return imageRaw
        /*return makeThumbnail(
            screenW,
            screenH,
            imageWidth,
            imageHeight,
            imageRaw
        )*/

    }

}
