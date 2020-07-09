package com.cups.splashin.peer2party.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cups.splashin.peer2party.R
import java.util.*


class PeersRecyclerAdapter internal constructor(
    private val context: Context,
    private val inflater: LayoutInflater = LayoutInflater.from(context)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var viewHolder: RecyclerView.ViewHolder
    private var peerList = emptyList<String>()

    class PeerViewHolder(
        view: View,
        val peerName: TextView = view.findViewById(R.id.peer_name),
        val peerPort: TextView = view.findViewById(R.id.peer_port),
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    ) : RecyclerView.ViewHolder(view)

    fun setPeers(peers: LinkedList<String>) {

        this.peerList = peers
        Log.d("fuck", "recycler peerList: $peerList")

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        viewHolder =
            PeerViewHolder(
                inflater.inflate(
                    R.layout.peer_inflatable,
                    parent,
                    false
                )
            )
        return viewHolder
    }

    override fun getItemCount(): Int {
        return peerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("fuck", "onBind called")
        (holder as PeerViewHolder).peerName.text = peerList[0]
        holder.peerPort.text = peerList[1]
    }
}