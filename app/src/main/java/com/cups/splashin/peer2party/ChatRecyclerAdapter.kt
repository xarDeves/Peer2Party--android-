package com.cups.splashin.peer2party

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cups.splashin.peer2party.data.DataBaseHolder
import com.cups.splashin.peer2party.data.EntityDataClass
import java.io.File


class ChatRecyclerAdapter internal constructor(private val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    private lateinit var file: File
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var viewHolder: ViewHolder
    private lateinit var view: View
    private lateinit var clipboard: ClipboardManager
    private lateinit var clip: ClipData
    private lateinit var uri: String

    private var allMessages = emptyList<EntityDataClass>()
    //private var clicked: MutableList<Boolean> = mutableListOf()

    private var messageArraySize: Int? = null
    private val RECEIVE_TEXT = 0
    private val SEND_TEXT = 1
    private val RECEIVE_IMAGE = 2
    private val SEND_IMAGE = 3

    /*private var screenW: Int? = null
    private var screenH: Int? = null

    internal fun setScreenDimensions(width: Int, height: Int) {

        this.screenW = width
        this.screenH = height
    }*/

    //goto to MainActivityViewModel and ChatFragment for the observable
    //(chain of events backtracks to "DbDao" -> "Repository")
    internal fun setMessages(entities: List<EntityDataClass>) {

        this.allMessages = entities
        this.messageArraySize = this.allMessages.size
        //this.clicked.add(false)

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return allMessages[position].layoutRes
    }

    override fun getItemCount(): Int {

        return allMessages.size
    }


    //TODO implement delete functionality for all ViewHolders
    class ImageSendViewHolder(
        view: View,
        val imageView: ImageView = view.findViewById(R.id.imageViewSend)
    ) : RecyclerView.ViewHolder(view)

    inner class ImageReceivedViewHolder(
        view: View,
        val imageView: ImageView = view.findViewById(R.id.imageViewReceived),
        val alias: TextView = view.findViewById(R.id.userTextImage),
        val delete: ImageView = view.findViewById(R.id.deleteImageRec),
        val info: LinearLayout = view.findViewById(R.id.infoImageRec),
        val date: TextView = view.findViewById(R.id.dateImageRec),
        val size: TextView = view.findViewById(R.id.sizeImageRec)
    ) : RecyclerView.ViewHolder(view) /*{
        init {

            if (clicked[adapterPosition + 1]) {
                delete.visibility = View.VISIBLE
                info.visibility = View.VISIBLE
                //clicked[position] = false
            } else {
                delete.visibility = View.GONE
                info.visibility = View.GONE
                //clicked[position] = true
            }
        }
    }*/


    class TextSendViewHolder(
        view: View, val textView: TextView = view.findViewById(R.id.chatTextSend)
    ) : RecyclerView.ViewHolder(view)


    class TextReceivedViewHolder(
        view: View,
        val textView: TextView = view.findViewById(R.id.chatTextRec),
        val alias: TextView = view.findViewById(R.id.userText),
        val delete: ImageView = view.findViewById(R.id.deleteChatRec),
        val info: LinearLayout = view.findViewById(R.id.infoChatRec),
        val date: TextView = view.findViewById(R.id.dateChatRec)
    ) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        when (viewType) {
            RECEIVE_TEXT -> {
                view = inflater.inflate(R.layout.chat_head_recieve, parent, false)
                viewHolder =
                    TextReceivedViewHolder(view)

            }
            SEND_TEXT -> {
                view = inflater.inflate(R.layout.chat_head_send, parent, false)
                viewHolder =
                    TextSendViewHolder(view)

            }
            RECEIVE_IMAGE -> {
                view = inflater.inflate(R.layout.image_recieve, parent, false)
                viewHolder =
                    ImageReceivedViewHolder(view)
            }
            SEND_IMAGE -> {
                view = inflater.inflate(R.layout.image_send, parent, false)
                viewHolder = ImageSendViewHolder(view)
            }
        }

        return viewHolder
    }

    //TODO fix visibility bugs
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (holder.itemViewType) {
            RECEIVE_TEXT -> {
                (holder as TextReceivedViewHolder).textView.text = allMessages[position].payload
                holder.alias.text = context.getString(
                    R.string.username, allMessages[position].alias
                )
                holder.date.text = allMessages[position].date

                holder.delete.setOnClickListener {

                    //holder.delete.visibility = View.GONE

                    val dao = DataBaseHolder.getInstance(context).dao()

                    val dbOperation = Thread { dao.delete(allMessages[position].key!!) }
                    dbOperation.start()
                    dbOperation.join()

                    notifyItemRemoved(position)
                }

                //copy text to clipboard:
                holder.itemView.setOnClickListener {

                    clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clip = ClipData.newPlainText("copied", holder.textView.text)
                    clipboard.primaryClip = clip

                    holder.delete.visibility = View.VISIBLE
                    holder.info.visibility = View.VISIBLE
                }

                //hiding username if the same user sends multiple message at once (guess what. It doesn't work):
                /*if (messageArraySize!! > 1) {
                    if (allMessages[messageArraySize!! - 1].alias == allMessages[messageArraySize!! - 2].alias) {
                        holder.alias.visibility = View.GONE
                        android.os.Handler().postDelayed({ notifyItemChanged(position) },1000)
                    }
                }*/
            }
            SEND_TEXT -> {
                (holder as TextSendViewHolder).textView.text = allMessages[position].payload
            }

            //goto OnActivityResult in ChatFragment, algorithm works only for fetching an image's
            //true path.
            RECEIVE_IMAGE -> {

                uri = allMessages[position].payload!!
                file = File(uri)
                if (file.exists()) {
                    //(holder as ImageReceivedViewHolder).imageView.setImageBitmap(makeThumbnail(screenW!!, screenH!!, Uri.parse(uri)))
                    Glide.with(context)
                        .load(uri)
                        .override(500, 500)
                        .fitCenter()
                        .dontAnimate()
                        .dontTransform()
                        .into((holder as ImageReceivedViewHolder).imageView)
                } else {
                    (holder as ImageReceivedViewHolder).imageView.setImageResource(R.drawable.not_found)
                }
                holder.alias.text =
                    context.getString(R.string.username, allMessages[position].alias)
                holder.date.text = allMessages[position].date
                holder.size.text = allMessages[position].size

                if (allMessages[position].clicked) {
                    holder.delete.visibility = View.VISIBLE
                    holder.info.visibility = View.VISIBLE
                } else {
                    holder.delete.visibility = View.GONE
                    holder.info.visibility = View.GONE
                }

                holder.delete.setOnClickListener {

                    val dao = DataBaseHolder.getInstance(context).dao()

                    val dbOperation = Thread { dao.delete(allMessages[position].key!!) }
                    dbOperation.start()
                    dbOperation.join()

                    notifyItemRemoved(position)

                }

                holder.itemView.setOnClickListener {

                    val dao = DataBaseHolder.getInstance(context).dao()

                    if (allMessages[position].clicked) {
                        holder.delete.visibility = View.GONE
                        holder.info.visibility = View.GONE
                        val dbOperation =
                            Thread { dao.updateChecked(false, allMessages[position].key!!) }
                        dbOperation.start()
                        dbOperation.join()
                    } else {
                        holder.delete.visibility = View.VISIBLE
                        holder.info.visibility = View.VISIBLE
                        val dbOperation =
                            Thread { dao.updateChecked(true, allMessages[position].key!!) }
                        dbOperation.start()
                        dbOperation.join()
                    }

                    //notifyItemChanged(position)

                    //holder.info.visibility = View.VISIBLE
                    //holder.delete.visibility = View.VISIBLE
                }

            }

            SEND_IMAGE -> {
                //(holder as ImageSendViewHolder).imageView.setImageURI(Uri.parse((allMessages[position].payload)))
                uri = allMessages[position].payload!!
                file = File(uri)
                if (file.absoluteFile.exists()) {
                    (holder as ImageSendViewHolder).imageView.setImageURI(Uri.parse(uri))
                } else {
                    (holder as ImageSendViewHolder).imageView.setImageResource(R.drawable.not_found)
                }
            }
        }
    }

}
