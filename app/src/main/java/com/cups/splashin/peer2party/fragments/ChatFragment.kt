package com.cups.splashin.peer2party.fragments


import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.data.MessageDataClass
import com.cups.splashin.peer2party.data.Repository
import com.cups.splashin.peer2party.functionality.RecyclerAdapter
import com.cups.splashin.peer2party.functionality.Sender
import com.cups.splashin.peer2party.functionality.makeThumbnail
import com.cups.splashin.peer2party.viewmodels.MainActivityViewModel
import java.io.InputStream

class ChatFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private val IMAGE_PICK_CODE = 1

    private var typeIndentifier: Int? = null
    private lateinit var explorerStream: InputStream
    private lateinit var viewModel: ViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var textToSend: String
    private lateinit var selectFilebtn: Button
    private lateinit var textEntry: TextView
    private lateinit var usernameHolder: TextView
    private lateinit var adapter: RecyclerAdapter


    lateinit var recipientsBtn: Button
    private lateinit var sendBtn: Button

    private fun initTextSend() {

        textToSend = textEntry.text.toString()
        Thread {

            if (Sender.sendText(textToSend)) {
                (viewModel as MainActivityViewModel).insertEntity(
                    MessageDataClass(1, textToSend, null)
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

    private fun initImageSend() {

        val decodedImage = BitmapFactory.decodeStream(explorerStream)
        (viewModel as MainActivityViewModel).insertEntity(
            MessageDataClass(3, null, explorerStream.readBytes())
        )

        Thread {
            Sender.sendImage(decodedImage)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            val path = data.data.lastPathSegment
            val dataRaw = data.data
            explorerStream = activity!!.contentResolver.openInputStream(dataRaw)
            Log.d("fuck", path)

            when {
                "image" in path -> {
                    //initTextSend()
                    initImageSend()
                }
                "wav" in path -> {
                    Log.d("fuck", "attempting to send wav")
                    typeIndentifier = 2
                    initAudioSend()

                }
                "mp3" in path -> {
                    Log.d("fuck", "attempting to send mp3")
                    typeIndentifier = 3
                    initAudioSend()

                }
            }
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
        usernameHolder = view.findViewById(R.id.username)
        recipientsBtn = view.findViewById(R.id.recipients)

        recycler = view.findViewById(R.id.recycler)
        layoutManager =  LinearLayoutManager(activity!!)
        layoutManager.stackFromEnd = true
        recycler.layoutManager = layoutManager
        adapter = RecyclerAdapter(activity!!)
        recycler.adapter = adapter

        (viewModel as MainActivityViewModel).allMessages.observe(activity!!, Observer {
            adapter.setMessages(it)
            layoutManager.scrollToPosition(it.size - 1)
        })

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

        return view

    }

}