package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MemoryCardGameAdapter(private val context: MemoryCardGameActivity) :
    RecyclerView.Adapter<MemoryCardGameAdapter.ViewHolder>()
{
    var datas = mutableListOf<MemoryCardGameData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_memory,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)

        holder.itemView.setOnClickListener() {

        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val card: ImageView = itemView.findViewById(R.id.img_rv_memory)
        fun bind(item: MemoryCardGameData, position: Int) {
            if (item.selected)
                card.setImageResource(item.imageID)
            else
                card.setImageResource(R.drawable.card_back)
            if (item.invisible) {
                card.visibility = View.INVISIBLE
                card.isEnabled = false
            }
            else {
                card.visibility = View.VISIBLE
                card.isEnabled = true
            }

            card.setOnClickListener() {
                /* card flip */
                item.selected = true
                notifyItemChanged(position)
                context.flipCard(position)
            }
        }
    }

}