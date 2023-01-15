package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class LeftRightAdapter(private val context: LeftRightActivity) :
    RecyclerView.Adapter<LeftRightAdapter.ViewHolder>()
{
    var datas = mutableListOf<LeftRightData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_leftright,parent,false)
        return ViewHolder(view)
    }

    //override fun getItemCount(): Int = datas.size
    override fun getItemCount(): Int = 8
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        if (position == 7)
            holder.itemView.setBackgroundResource(R.drawable.textview_edge)
        else
            holder.itemView.setBackgroundResource(0)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = itemView.findViewById(R.id.img_rv_leftright)
        fun bind(item: LeftRightData, position: Int) {
            //img.text = item.name
            //imageView.setImageResource(R.drawable.mushroom_blue)
            imageView.setImageResource(item.imageID)
        }
    }

}