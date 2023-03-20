package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/*class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = Int.MAX_VALUE
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
}*/
class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    private lateinit var myViewPager : ViewPager2
    private lateinit var context: Context
    var datas = mutableListOf<GameHelpData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_gamehelp,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE /* CHECK */

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position % datas.size], position)
        // auto sliding
        /*if (datas.size > 1) {
            if (position == datas.size - 1)
                myViewPager.post(runnable)
        }*/
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTv: TextView = itemView.findViewById(R.id.tv_game_title)
        private val descriptionTv: TextView = itemView.findViewById(R.id.tv_game_des)
        fun bind(item: GameHelpData, position: Int) {
            titleTv.text = item.title
            descriptionTv.text = item.description
        }
    }

    fun setSliderList(context: Context, viewPager2: ViewPager2, pagerItems: MutableList<GameHelpData>) {
        this.context = context
        this.myViewPager = viewPager2
        this.datas = pagerItems
        notifyDataSetChanged()
    }
}