package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class WhackAMoleAdapter(private val context: WhackAMoleActivity) :
    RecyclerView.Adapter<WhackAMoleAdapter.ViewHolder>()
{
    var datas = mutableListOf<WhackAMoleData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_whack_a_mole,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)

        holder.itemView.setOnClickListener() {

        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mole: ImageView = itemView.findViewById(R.id.img_rv_mole)
        fun bind(item: WhackAMoleData, position: Int) {
            mole.setImageResource(item.imageID)
            if (item.selected) {
                mole.visibility = View.VISIBLE
                mole.isEnabled = true
            }
            else {
                mole.visibility = View.INVISIBLE
                mole.isEnabled = false
            }
            mole.setOnClickListener() {
                var res = 0
                when (datas[position].imageID) {
                    R.drawable.mokoko -> res = 1
                    R.drawable.mokoko_g -> res = 3
                    R.drawable.mushroom_z -> res = -5
                }
                context.popMole(res, position)
            }
        }
    }

}