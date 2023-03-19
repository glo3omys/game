package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 8
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> PageTaptap()
            1 -> PageBalloon()
            2 -> PageFindnum()
            3 -> PageMath()
            4 -> PageLeftRight()
            5 -> PageMemory()
            6 -> PageWhack()
            else -> PageInitial()
        }
    }
}