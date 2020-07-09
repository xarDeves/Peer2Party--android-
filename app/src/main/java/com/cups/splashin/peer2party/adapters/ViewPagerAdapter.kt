package com.cups.splashin.peer2party.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cups.splashin.peer2party.MainActivityViewModel

class ViewPagerAdapter(
    manager: FragmentManager,
    private val viewModel: MainActivityViewModel
) :
    FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            viewModel.fragmentA
        } else {
            viewModel.fragmentB
        }
    }

    override fun getCount(): Int {
        return 2
    }

}