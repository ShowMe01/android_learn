package com.example.helloworld.cmb

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return SessionsFragment()
            }

            1 -> {
                return ContactsFragment()
            }

            2 -> {
                return ProfileFragment()
            }

            else -> {
                return SessionsFragment()
            }
        }
    }
}