package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MathAdapter(private val context: MathActivity) :
    RecyclerView.Adapter<MathAdapter.ViewHolder>()
{
    var datas = mutableListOf<MathData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_math,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        if (datas[position].selected)
            holder.itemView.visibility = View.INVISIBLE
        else
            holder.itemView.visibility = View.VISIBLE
        /*
        holder.itemView.setOnClickListener() {
            datas[position].selected = !datas[position].selected
            notifyItemChanged(position)
        }
        */
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val number: TextView = itemView.findViewById(R.id.btn_rv_math)
        fun bind(item: MathData, position: Int) {
            number.text = item.num.toString()

            itemView.setOnClickListener {
                datas[position].selected = !datas[position].selected
                notifyItemChanged(position)

                context.popNumber(datas[position].num)
            }
        }

    }
}