package com.example.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class BalloonAdapter(private val context: Context) :
    RecyclerView.Adapter<BalloonAdapter.ViewHolder>()
{

    var datas = mutableListOf<BalloonData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.balloon_recycler,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        when (datas[position].name) {
            1 -> holder.itemView.setBackgroundColor(Color.RED)
            2 -> holder.itemView.setBackgroundColor(Color.BLUE)
            3 -> holder.itemView.setBackgroundColor(Color.GRAY)
            4 -> holder.itemView.setBackgroundColor(Color.GREEN)
            5 -> holder.itemView.setBackgroundColor(Color.YELLOW)
        }
        /*holder.itemView.setOnClickListener() {

        }

         */
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val balloon: Button = itemView.findViewById(R.id.btn_rv)
        fun bind(item: BalloonData, position: Int) {
            balloon.text = item.name.toString()
        }
    }

}