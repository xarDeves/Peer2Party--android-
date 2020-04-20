package com.cups.splashin.peer2party.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.viewmodels.MainActivityViewModel

//TODO fetch clicked users
class PeerListFragment : Fragment() {

    private lateinit var viewModel: MainActivityViewModel

    private fun initList(peerList: MutableList<String>) {

        viewModel.peerListView.adapter = ArrayAdapter(
            context,
            R.layout.peer_inflatable,
            peerList
        )

        viewModel.peerListView.setOnItemClickListener { parent, view, position, id ->

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.peer_list_fragment, container, false)

        viewModel = ViewModelProvider(activity!!).get(MainActivityViewModel::class.java)

        viewModel.peerListView = view.findViewById(R.id.peerListHolder)
        viewModel.peerListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        initList(viewModel.peerList)

        return view
    }


}





