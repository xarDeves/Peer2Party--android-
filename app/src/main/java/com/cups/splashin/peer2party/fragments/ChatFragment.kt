package com.cups.splashin.peer2party.fragments


import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cups.splashin.peer2party.ChatRecyclerAdapter
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.data.EntityDataClass
import com.cups.splashin.peer2party.fetchDateTime
import com.cups.splashin.peer2party.functionality.Saver
import com.cups.splashin.peer2party.functionality.Sender
import com.cups.splashin.peer2party.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.chat_fragment.*
import java.io.File
import java.io.InputStream

//TODO refactor the "init" functions, saver, sender objects, and path handling/filtering (waiting for netcode)
class ChatFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private val IMAGE_PICK_CODE = 1
    private val IMAGE_CAPTURE_CODE = 2
    private val VIDEO_CAPTURE_CODE = 3

    var clicked = false
    private lateinit var imageUri: Uri


    private var typeIndentifier: Int? = null
    private lateinit var explorerStream: InputStream
    private lateinit var viewModel: ViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var textToSend: String
    private lateinit var selectFilebtn: Button
    private lateinit var openCaptureBtn: Button
    private lateinit var openImageBtn: Button
    private lateinit var openVideoBtn: Button
    private lateinit var selectMode: TableLayout
    private lateinit var textEntry: TextView
    private lateinit var usernameHolder: TextView
    private lateinit var chatAdapter: ChatRecyclerAdapter


    lateinit var recipientsBtn: Button
    private lateinit var sendBtn: Button

    private fun initTextSend() {

        textToSend = textEntry.text.toString()
        Thread {
            if (Sender.sendText(textToSend)) {
                (viewModel as MainActivityViewModel).insertEntity(
                    EntityDataClass(1, textToSend, fetchDateTime())
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
            Sender.sendAudio(typeIndentifier!!, explorerStream)
        }.start()
    }

    //TODO make everything dynamic...obviously
    //works only for images:
    private fun realPathImage(
        contentUri: Uri?,
        resolver: ContentResolver
    ): String? {
        var cursor: Cursor = resolver.query(contentUri, null, null, null, null)
        cursor.moveToFirst()
        var documentId: String = cursor.getString(0)
        documentId = documentId.substring(documentId.lastIndexOf(":") + 1)
        cursor.close()
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null
            , MediaStore.Images.Media._ID + " = ? ", arrayOf(documentId), null
        )
        cursor.moveToFirst()
        val path: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                val uri: Uri = data!!.data
                val contentResolver = activity!!.contentResolver
                val absoluteUri = realPathImage(uri, contentResolver)
                val type = contentResolver.getType(uri)
                explorerStream = activity!!.contentResolver.openInputStream(uri)

                when {
                    "image" in type -> {
                        //initTextSend()
                        initImageSend(absoluteUri!!)
                    }
                    "wav" in type -> {
                        Log.d("fuck", "attempting to send wav")
                        typeIndentifier = 2
                        initAudioSend()

                    }
                    "mp3" in type -> {
                        Log.d("fuck", "attempting to send mp3")
                        typeIndentifier = 3
                        initAudioSend()

                    }
                }
            }
        } else if (requestCode == 2) {
            //if (resultCode == RESULT_OK) {
                //activity!!.contentResolver.notifyChange(imageUri, null);
                val bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                    activity!!.contentResolver,
                    imageUri
                )
                Thread {
                    (viewModel as MainActivityViewModel).insertEntity(
                        EntityDataClass(
                            3,
                            imageUri.toString(),
                            fetchDateTime(),
                            null,
                            android.text.format.Formatter.formatFileSize(
                                activity!!,
                                Saver.saveImage(bitmap).toLong()
                            )
                        )
                    )
                }.start()

           // }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)

        viewModel = ViewModelProvider(activity!!).get(MainActivityViewModel::class.java)

        selectFilebtn = view.findViewById(R.id.selectFile)
        textEntry = view.findViewById(R.id.textInput)
        sendBtn = view.findViewById(R.id.buttonSend)
        openCaptureBtn = view.findViewById(R.id.openCapture)
        openImageBtn = view.findViewById(R.id.openImage)
        openVideoBtn = view.findViewById(R.id.openVideo)
        selectMode = view.findViewById(R.id.selectCaptureMode)
        usernameHolder = view.findViewById(R.id.username)
        recipientsBtn = view.findViewById(R.id.recipients)

        //recycler setup:
        recycler = view.findViewById(R.id.recycler)
        layoutManager = LinearLayoutManager(activity!!)
        //layoutManager.stackFromEnd = true
        recycler.layoutManager = layoutManager
        chatAdapter = ChatRecyclerAdapter(activity!!)
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
        )*/

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recipientsBtn.visibility = View.GONE
        }

        //for send text:
        sendBtn.setOnClickListener {
            initTextSend()
        }

        recipientsBtn.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            val transactionManager = fragmentManager.beginTransaction()
            transactionManager.add(
                R.id.chatFrame,
                (viewModel as MainActivityViewModel).fragmentB,
                "B"
            )
            transactionManager.addToBackStack("fragmentStack")
            transactionManager.commit()

            //Log.d("fuck", fragmentManager.backStackEntryCount.toString())

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