package com.example.qp

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileVPAdapter(fragment: ProfileActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ProfileMyQuestionFragment()
            1 -> ProfileBuyQuestionFragment()
            else -> ProfileNoticeQuestionFragment()
        }
    }

}