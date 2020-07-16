package com.cups.splashin.peer2party.android.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.android.app.MainActivityViewModel
import com.cups.splashin.peer2party.android.app.adapters.PeersRecyclerAdapter

//TODO fetch clicked users, retain state on orientation change
class PeerListFragment : Fragment() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recycler: RecyclerView
    lateinit var peersAdapter: PeersRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.peer_list_fragment, container, false)

        viewModel = ViewModelProvider(activity!!).get(MainActivityViewModel::class.java)

        //recycler setup:
        recycler = view.findViewById(R.id.peersRecycler)
        layoutManager = LinearLayoutManager(activity!!)
        recycler.layoutManager = layoutManager
        peersAdapter = PeersRecyclerAdapter(activity!!)
        recycler.adapter = peersAdapter

        Log.d("fuck", "create view")

        viewModel.peers?.let { peersAdapter.setPeers(it) }

        return view
    }

}





