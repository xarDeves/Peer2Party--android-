package com.cups.splashin.peer2party.viewmodels

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.ListView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cups.splashin.peer2party.data.DataBaseHolder
import com.cups.splashin.peer2party.data.DbDao
import com.cups.splashin.peer2party.data.EntityDataClass
import com.cups.splashin.peer2party.data.Repository
import com.cups.splashin.peer2party.fetchDateTime
import com.cups.splashin.peer2party.fragments.ChatFragment
import com.cups.splashin.peer2party.fragments.PeerListFragment
import com.cups.splashin.peer2party.functionality.Reader
import com.cups.splashin.peer2party.functionality.Saver
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    override fun onCleared() {
        super.onCleared()

        println("fuck")
    }

    private var options: BitmapFactory.Options? = null

    //private lateinit var messageByteClean: ByteArray
    private val sServ: ServerSocket = ServerSocket(7000)
    private lateinit var sRec: Socket
    private lateinit var dataIs: DataInputStream
    private var identifier: Int? = null

    private var buffer: ByteArray = ByteArray(512)
    private lateinit var messageByte: ByteArray
    private lateinit var inputStream: InputStream
    var screenH: Int? = null
    var screenW: Int? = null

    //init fragments
    val fragmentA = ChatFragment()
    val fragmentB = PeerListFragment()

    //for PeerList fragment
    lateinit var peerListView: ListView
    var peerList = mutableListOf<String>()

    private val repository: Repository
    private var dao: DbDao = DataBaseHolder.getInstance(application).dao()
    var allMessages: LiveData<List<EntityDataClass>>

    fun insertEntity(entity: EntityDataClass) {
        viewModelScope.launch {
            dao.insert(entity)
        }
    }

    init {

        repository = Repository(dao)
        allMessages = repository.allMessages

        Thread {
            while (true) {
                sRec = sServ.accept()

                inputStream = sRec.getInputStream()

                messageByte = inputStream.readBytes()
                identifier = messageByte[0].toInt()
                messageByte = messageByte.copyOfRange(1, messageByte.size)
                /*
                dataIs = DataInputStream(BufferedInputStream(sRec.getInputStream()))
                identifier = dataIs.readInt()

                val buffer = ByteArray(1024)
                var read: Int
                inputStream.read(buffer)
                val firstChunk = buffer.copyOfRange(1, buffer.lastIndex)

                //for progress bar. Need to know the file size first
                //i need the network manager code:
                messageByte = inputStream.read(buffer)

                while (inputStream.read(buffer) != -1){
                    messageByte += buffer
                    observableProgressUpdater.postValue()
                }*/

                when (identifier) {
                    0 -> {
                        //goto reader for testing purposes:
                        Log.d("fuck", "A")
                        val text = Reader.readText(messageByte, inputStream)
                        if (text.isNotBlank() && text.isNotEmpty()) {
                            //auto copy to clipboard on receive (i just want to check something):
                            val clipboard =
                                application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("copied", text)
                            clipboard.primaryClip = clip
                            insertEntity(
                                EntityDataClass(
                                    0,
                                    text,
                                    fetchDateTime(),
                                    "receiveText"
                                )
                            )
                        }
                    }
                    1 -> {
                        options = BitmapFactory.Options().apply { inJustDecodeBounds = true }

                        //in order to get width and height:
                        /*BitmapFactory.decodeByteArray(
                            messageByte,
                            0,
                            messageByte.size,
                            options
                        )*/

                        val imageHeight = options!!.outHeight
                        val imageWidth = options!!.outWidth

                        if (imageHeight != -1) {
                            val image = Reader.readImage(
                                messageByte,
                                screenW!!,
                                screenH!!,
                                imageWidth,
                                imageHeight
                            )
                            val uri = Saver.saveImage(image)
                            insertEntity(
                                EntityDataClass(
                                    2,
                                    uri,
                                    fetchDateTime(),
                                    "receiveImage",
                                    android.text.format.Formatter.formatFileSize(
                                        application,
                                        messageByte.size.toLong()
                                    )
                                )
                            )
                        }
                    }
                    2 -> {
                        Saver.saveAudio(messageByte)
                    }
                    3 -> {
                        Log.d("fuck", "attempting to save video")
                        Saver.currentTime = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault())
                        Saver.timeString =
                            Saver.currentTime.format(System.currentTimeMillis()).toString()
                        val path = Environment.getExternalStorageDirectory().absolutePath
                        //change path:
                        val dir = File("$path/DCIM/")
                        dir.mkdirs()
                        //change extension:
                        Saver.file = File(dir, "${Saver.currentTime}.mp4")
                        val fos = FileOutputStream(Saver.file)
                        fos.write(messageByte)
                        fos.flush()
                        fos.close()
                        Log.d("fuck", "video saved successfully")
                        //Saver.saveVideo(messageByte)
                        //observableImage.postValue(entityDataClasses.last())
                    }
                }


            }
        }.start()
    }

}

