package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

//class BalloonAdapter(private val context: Context) :
class BalloonAdapter(private val context: BalloonActivity) :
    RecyclerView.Adapter<BalloonAdapter.ViewHolder>()
{
    var datas = mutableListOf<BalloonData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_balloon,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        when (datas[position].name.toString()) {
            "RED" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.RED.RGB.toString()))
            "ORANGE" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.ORANGE.RGB.toString()))
            "YELLOW" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.YELLOW.RGB.toString()))
            "GREEN" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.GREEN.RGB.toString()))
            "BLUE" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.BLUE.RGB.toString()))
        }

        holder.itemView.setOnClickListener() {
            /* compare color, score++ */
            //Toast.makeText(this.context, position.toString(), Toast.LENGTH_SHORT).show()
            holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.BLACK.RGB.toString()))
            context.popBalloon(datas[position].name)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val balloon: Button = itemView.findViewById(R.id.btn_rv)
        fun bind(item: BalloonData, position: Int) {
            balloon.text = item.name
        }

    }

}