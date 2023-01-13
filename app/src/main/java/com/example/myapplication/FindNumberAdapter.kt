package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class FindNumberAdapter(private val context: FindNumberActivity) :
    RecyclerView.Adapter<FindNumberAdapter.ViewHolder>()
{
    var datas = mutableListOf<FindNumberData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_findnum,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        if (datas[position].selected)
            holder.itemView.visibility = View.INVISIBLE
        else
            holder.itemView.visibility = View.VISIBLE

        holder.itemView.setOnClickListener() {
            //Toast.makeText(this.context, position.toString(), Toast.LENGTH_SHORT).show()
            //holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.BLACK.RGB.toString()))
            /*
            val flag = context.popNumber(datas[position])
            if (flag)
                holder.itemView.visibility = View.INVISIBLE
            */
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val number: Button = itemView.findViewById(R.id.btn_rv_number)
        fun bind(item: FindNumberData, position: Int) {
            number.text = item.num.toString()
            number.setOnClickListener() {
                val flag = context.popNumber(datas[position].num)
                if (flag) {
                    datas[position].selected = !datas[position].selected
                    notifyItemChanged(position)
                }
            }
        }

    }

}