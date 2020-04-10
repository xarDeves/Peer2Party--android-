package com.cups.splashin.peer2party.functionality

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object Saver {

    lateinit var currentTime: SimpleDateFormat
    lateinit var timeString: String
    lateinit var file: File

    //make "saveVideo" and "saveAudio" in to one method:
    fun saveVideo(messageByte: ByteArray) {
        Log.d("fuck", "attempting to save video")
        currentTime = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault())
        timeString = currentTime.format(System.currentTimeMillis()).toString()
        val path = Environment.getExternalStorageDirectory().absolutePath
        //change path:
        val dir = File("$path/DCIM/")
        dir.mkdirs()
        //change extension:
        file = File(dir, "$currentTime.mp4")
        val fos = FileOutputStream(file)
        fos.write(messageByte)
        fos.flush()
        fos.close()
        Log.d("fuck", "video saved successfully")

    }

    fun saveAudio(messageByte: ByteArray) {
        Log.d("fuck", "attempting to save audio")
        currentTime = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault())
        timeString = currentTime.format(System.currentTimeMillis()).toString()
        val path = Environment.getExternalStorageDirectory().absolutePath
        //change path:
        val dir = File("$path/DCIM/")
        dir.mkdirs()
        //change extension:
        file = File(dir, "$currentTime.mp3")
        val fos = FileOutputStream(file)
        fos.write(messageByte)
        fos.flush()
        fos.close()
        Log.d("fuck", "audio saved successfully")

    }

    fun saveImage(image: Bitmap) {

        currentTime = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault())
        timeString = currentTime.format(System.currentTimeMillis()).toString()
        val path = Environment.getExternalStorageDirectory().absolutePath
        val dir = File("$path/DCIM/")
        dir.mkdirs()
        val file = File(dir, "$currentTime.jpg")
        val fos = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

    }
}