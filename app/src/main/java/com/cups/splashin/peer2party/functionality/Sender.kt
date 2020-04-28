package com.cups.splashin.peer2party.functionality

import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.PrintWriter
import java.net.Socket

object Sender {

    private lateinit var pw: PrintWriter
    private var sSend: Socket? = null
    private const val ipToConnect: String = "192.168.1.7"

    fun sendText(textToSend: String): Boolean {
        if (!(textToSend.isBlank() || textToSend.isEmpty() || ipToConnect.isBlank() || ipToConnect.isEmpty())) {
            return try {
                sSend = Socket(ipToConnect, 7000)
                pw = PrintWriter(sSend!!.getOutputStream())
                pw.write(0)
                pw.write(textToSend)
                pw.flush()
                pw.close()
                sSend!!.close()
                true
            } catch (t: Throwable) {
                t.printStackTrace()
                false
            }
        } else {
            return false
        }
    }

    fun sendImage(image: Bitmap): Int {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, baos)
        val imageByte = baos.toByteArray()

        try {

            sSend = Socket(ipToConnect, 7000)
            val os = sSend!!.getOutputStream()
            val dos = DataOutputStream(os)
            dos.write(1)
            dos.write(imageByte, 0, imageByte.size)
            dos.close()
            os.close()
            sSend!!.close()
            Log.d("fuck", "image sent successful")
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        return imageByte.size

    }

    fun sendAudio(inputStream1: Int, inputStream: InputStream) {

        try {

            val rawData = inputStream.readBytes()
            //val baos = ByteArrayOutputStream()
            sSend = Socket(ipToConnect, 7000)
            val os = sSend!!.getOutputStream()
            val dos = DataOutputStream(os)
            dos.write(2)
            dos.write(rawData)
            dos.close()
            os.close()
            sSend!!.close()
            Log.d("fuck", "audio sent successful")
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    //deprecated. Waiting for network manager:
    /*
    //in order for the server (pc) to automatically set this device's IP address:
    private fun sendMock() {

        Thread {
            try {
                sSend = Socket(ipToConnect, 7000)
                pw = PrintWriter(sSend!!.getOutputStream())
                pw.write("")
                pw.flush()
                pw.close()
            } catch (t: Throwable) {
                t.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "IP Address Not Found", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()

    }
    */

}