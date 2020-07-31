package com.cups.splashin.peer2party.android.app.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.android.app.MainActivityViewModel
import com.cups.splashin.peer2party.android.app.adapters.ChatRecyclerAdapter
import com.cups.splashin.peer2party.android.app.data.EntityDataClass
import com.cups.splashin.peer2party.android.app.fetchDateTime
import com.cups.splashin.peer2party.android.app.functionality.Saver
import com.cups.splashin.peer2party.android.app.functionality.Sender
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.coroutines.Job
import java.io.File
import java.io.InputStream


//TODO refactor the "init" functions, saver, sender objects, and path handling/filtering (waiting for netcode)
class ChatFragment : Fragment() {

    lateinit var recTimer: Job

    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    private lateinit var layoutManager: LinearLayoutManager

    private val IMAGE_PICK_CODE = 1
    private val IMAGE_CAPTURE_CODE = 2
    private val VIDEO_CAPTURE_CODE = 3

    private var clicked = false
    private lateinit var imageUri: Uri

    private lateinit var audioRec: MediaRecorder


    private var typeIdentifier: Int? = null
    private lateinit var explorerStream: InputStream
    private lateinit var viewModel: ViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var textToSend: String
    private lateinit var selectFilebtn: Button
    private lateinit var openCaptureBtn: Button
    private lateinit var openImageBtn: Button
    private lateinit var openVideoBtn: Button
    private lateinit var recVoiceButton: ToggleButton
    private lateinit var selectMode: TableLayout
    private lateinit var textEntry: TextView
    private lateinit var usernameHolder: TextView
    private lateinit var recTable: TableLayout
    private lateinit var cancelRecButton: Button
    private lateinit var timeRecordedChrono: Chronometer
    private lateinit var chatAdapter: ChatRecyclerAdapter

    private lateinit var sendBtn: Button

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }

    private fun initTextSend() {

        textToSend = textEntry.text.toString()
        Thread {
            if (Sender.sendText(textToSend)) {
                (viewModel as MainActivityViewModel).insertEntity(
                    EntityDataClass(
                        1, textToSend,
                        fetchDateTime()
                    )
                )
                activity?.runOnUiThread {
                    textEntry.text = ""
                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error !", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()

    }

    private fun initImageSend(path: String) {

        Thread {
            Log.d("fuck", path)
            val size = Sender.sendImage(BitmapFactory.decodeStream(explorerStream))
            (viewModel as MainActivityViewModel).insertEntity(
                EntityDataClass(
                    3,
                    path,
                    fetchDateTime(),
                    null,
                    android.text.format.Formatter.formatFileSize(
                        activity!!,
                        size.toLong()
                    )
                )
            )
        }.start()


        /*Thread {
            //create thumbnail, display it and keep a copy for screen rotation purposes:
            val thumbnail =
                makeThumbnail(
                    (viewModel as MainActivityViewModel).screenW!!,
                    (viewModel as MainActivityViewModel).screenH!!,
                    decodedImage.width,
                    decodedImage.height,
                    decodedImage
                )
            (viewModel as MainActivityViewModel).insertEntity(
                MessageDataClass(3, null, explorerStream.readBytes())
            )
        }.start()*/


    }

    private fun initAudioSend() {
        Thread {
            Sender.sendAudio(typeIdentifier!!, explorerStream)
        }.start()
    }


    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri: Uri? = data?.data
        val contentResolver = activity!!.contentResolver
        if (uri != null) {
            val type = contentResolver.getType(uri)
            explorerStream = activity!!.contentResolver.openInputStream(uri)
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {

                    when {
                        "image" in type -> {
                            //initTextSend()
                            initImageSend(uri.toString())
                        }
                        "wav" in type -> {
                            Log.d("fuck", "attempting to send wav")
                            typeIdentifier = 2
                            initAudioSend()

                        }
                        "mp3" in type -> {
                            Log.d("fuck", "attempting to send mp3")
                            typeIdentifier = 3
                            initAudioSend()

                        }
                    }
                }
            } else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    //activity!!.contentResolver.notifyChange(imageUri, null);
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        activity!!.contentResolver,
                        uri
                    )
                    Thread {
                        (viewModel as MainActivityViewModel).insertEntity(
                            EntityDataClass(
                                3,
                                uri.toString(),
                                fetchDateTime(),
                                null,
                                android.text.format.Formatter.formatFileSize(
                                    activity!!,
                                    Saver.saveImage(bitmap).toLong()
                                )
                            )
                        )
                    }.start()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)

        viewModel = ViewModelProvider(activity!!).get(MainActivityViewModel::class.java)

        ActivityCompat.requestPermissions(activity!!, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        selectFilebtn = view.findViewById(R.id.selectFile)
        textEntry = view.findViewById(R.id.textInput)
        sendBtn = view.findViewById(R.id.buttonSend)
        openCaptureBtn = view.findViewById(R.id.openCapture)
        openImageBtn = view.findViewById(R.id.openImage)
        openVideoBtn = view.findViewById(R.id.openVideo)
        recVoiceButton = view.findViewById(R.id.recVoice)
        selectMode = view.findViewById(R.id.selectCaptureMode)
        usernameHolder = view.findViewById(R.id.username)
        recTable = view.findViewById(R.id.recTable)
        cancelRecButton = view.findViewById(R.id.cancelRec)
        timeRecordedChrono = view.findViewById(R.id.timeRecorded)

        //recycler setup:
        recycler = view.findViewById(R.id.chatRecycler)
        layoutManager = LinearLayoutManager(activity!!)
        //layoutManager.stackFromEnd = true
        recycler.layoutManager = layoutManager
        chatAdapter =
            ChatRecyclerAdapter(activity!!)
        recycler.adapter = chatAdapter

        usernameHolder.text = arguments!!.getString("ID")

        //goto RecyclerAdapter
        (viewModel as MainActivityViewModel).allMessages.observe(activity!!, Observer {
            chatAdapter.setMessages(it)
            if (layoutManager.findLastVisibleItemPosition() == it.size - 2) {
                layoutManager.scrollToPosition(it.size - 1)
            }
        })

        /*chatAdapter.setScreenDimensions(
            (viewModel as MainActivityViewModel).screenW!!,
            (viewModel as MainActivityViewModel).screenH!!
        )

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recipientsBtn.visibility = View.GONE
        }
         */

        //for send text:
        sendBtn.setOnClickListener {
            initTextSend()
        }


        recVoiceButton.setOnCheckedChangeListener { _, isChecked ->
            Log.d("fuck", isChecked.toString())
            if (isChecked) {
                if (!permissionToRecordAccepted) {

                    audioRec = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    }

                    timeRecordedChrono.base = SystemClock.elapsedRealtime()
                    recTable.visibility = View.VISIBLE
                    timeRecordedChrono.start()

                    //using coroutine and TextView:
                    /*recTable.visibility = View.VISIBLE
                    recTimer = GlobalScope.launch(Default) {
                        var secs = 1L
                        var hours: Int
                        var remainder: Int
                        var mins: Int

                        while (true) {
                            withContext(Main) {
                                hours = secs.toInt() / 3600
                                remainder = secs.toInt() - hours * 3600
                                mins = remainder / 60
                                remainder -= mins * 60

                                timeRecordedText.text = "$hours:$mins:$remainder"
                            }
                            secs++
                            Thread.sleep(1000)
                        }
                    }*/

                }

            } else {
                timeRecordedChrono.stop()
                recTable.visibility = View.GONE
            }
        }

        selectFilebtn.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        openCaptureBtn.setOnClickListener {

            if (clicked) {
                selectCaptureMode.visibility = View.GONE
                clicked = false
            } else {
                selectCaptureMode.visibility = View.VISIBLE
                clicked = true
            }

        }

        openImageBtn.setOnClickListener {

            val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val path = Environment.getExternalStorageDirectory()
            val photo = File("$path/DCIM/")
            if (!photo.exists()) {
                photo.mkdir()
            }
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo)
            )
            imageUri = Uri.fromFile(photo)
            startActivityForResult(intent, IMAGE_CAPTURE_CODE)
        }

        openVideoBtn.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, VIDEO_CAPTURE_CODE)
        }

        return view

    }

}