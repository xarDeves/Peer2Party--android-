package com.cups.splashin.peer2party.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cups.splashin.peer2party.PeerTransaction
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.adapters.PeersRecyclerAdapter
import com.cups.splashin.peer2party.viewmodels.MainActivityViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


//TODO fetch clicked users
class PeerListFragment : Fragment() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recycler: RecyclerView
    private lateinit var peersAdapter: PeersRecyclerAdapter

    override fun onResume() {
        super.onResume()
        peersAdapter.setPeers(viewModel.peers!!)

    }

    override fun onDestroy() {

        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onStart() {

        EventBus.getDefault().register(this)
        super.onStart()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: PeerTransaction) {
        viewModel.fetchPeers()
        peersAdapter.setPeers(viewModel.peers!!)
    }

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

        viewModel.peers?.let { peersAdapter.setPeers(it) }

        return view
    }

}





