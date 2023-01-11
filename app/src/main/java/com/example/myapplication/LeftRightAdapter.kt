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
    override fun getItemCount(): Int = 5
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        if (position == 4)
            holder.itemView.setBackgroundResource(R.drawable.textview_edge)

        /*
        when (datas[position].name.toString()) {
            "RIGHT" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.RED.RGB.toString()))
            "LEFT" -> holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.YELLOW.RGB.toString()))
        }
        */

        //holder.itemView.setOnClickListener() {
            //Toast.makeText(this.context, position.toString(), Toast.LENGTH_SHORT).show()
            //holder.itemView.setBackgroundColor(Color.parseColor(BalloonColors.BLACK.RGB.toString()))
            //context.popBalloon(datas[position].name)
        //}
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*
        private val imageView: ImageView = itemView.findViewById(R.id.img_leftright)
        fun bind(item: LeftRightData, position: Int) {
            this.item.setImageResource(item.imageID)
            imageView.setImageResource(R.drawable.mushroom_blue)
            this.item.setImageResource(R.drawable.mushroom_blue)
        }
        */
        //private val img: Button = itemView.findViewById(R.id.btn_rv_leftright)
        private val imageView: ImageView = itemView.findViewById(R.id.img_rv_leftright)
        fun bind(item: LeftRightData, position: Int) {
            //img.text = item.name
            //imageView.setImageResource(R.drawable.mushroom_blue)
            imageView.setImageResource(item.imageID)
        }
    }

}