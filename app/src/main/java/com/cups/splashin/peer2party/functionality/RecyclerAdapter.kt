package com.cups.splashin.peer2party.functionality

import android.R.attr.label
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.data.MessageDataClass


class RecyclerAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var viewHolder: ViewHolder
    private lateinit var view: View
    private var allMessages = emptyList<MessageDataClass>()

    private val RECEIVE_TEXT = 0
    private val SEND_TEXT = 1
    private val RECEIVE_IMAGE = 2
    private val SEND_IMAGE = 3

    class TextReceivedVH(val textView: TextView) : RecyclerView.ViewHolder(textView)
    class TextSendVH(val textView: TextView) : RecyclerView.ViewHolder(textView)
    class ImageReceivedVH(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    class ImageSendVH(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    internal fun setMessages(entities: List<MessageDataClass>) {
        this.allMessages = entities
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return allMessages[position].layoutRes
    }

    override fun getItemCount(): Int {
        Log.d("fuck", "${allMessages.size}, getItemCount")
        return allMessages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        when (viewType) {
            RECEIVE_TEXT -> {
                Log.d("fuck", "RECEIVE_TEXT")
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_head_recieve, parent, false)
                viewHolder = TextReceivedVH(view as TextView)

            }
            SEND_TEXT -> {
                Log.d("fuck", "SEND_TEXT")
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_head_send, parent, false)
                viewHolder = TextSendVH(view as TextView)

            }
            RECEIVE_IMAGE -> {
                Log.d("fuck", "RECEIVE_IMAGE")
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_recieve, parent, false)
                viewHolder = ImageReceivedVH(view as ImageView)
            }
            SEND_IMAGE -> {
                Log.d("fuck", "SEND_IMAGE")
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_send, parent, false)
                viewHolder = ImageSendVH(view as ImageView)

            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            RECEIVE_TEXT -> {
                Log.d("fuck", "0")
                (holder as TextReceivedVH).textView.text =
                    allMessages[position].text
            }
            SEND_TEXT -> {
                Log.d("fuck", "1")
                (holder as TextSendVH).textView.text =
                    allMessages[position].text
            }
            /*RECEIVE_IMAGE -> {
                Log.d("fuck", "2")
                (holder as ImageReceivedVH).imageView.setImageBitmap(allMessages[position].bytes as Bitmap)
            }
            SEND_IMAGE -> {
                Log.d("fuck", "3")
                (holder as ImageSendVH).imageView.setImageBitmap(allMessages[position].text as Bitmap)
            }*/
        }
    }

}
