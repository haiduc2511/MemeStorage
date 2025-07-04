package com.example.memestorage.adapterver2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.memestorage.fragments.FriendRequestReceivedFragment
import com.example.memestorage.fragments.FriendshipListFragment

class ProfileAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FriendshipListFragment() else FriendRequestReceivedFragment()
    }
}

