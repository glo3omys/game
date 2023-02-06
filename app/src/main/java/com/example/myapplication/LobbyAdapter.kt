package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LobbyAdapter(private val context: LobbyActivity) :
    RecyclerView.Adapter<LobbyAdapter.ViewHolder>()
{
    var datas = mutableListOf<UserData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_users,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = itemView.findViewById(R.id.img_rv_user)
        private val tvName: TextView = itemView.findViewById(R.id.tv_rv_username)

        fun bind(item: UserData, position: Int) {
            //img.text = item.name
            tvName.text = item.name
            //imageView.setImageResource(R.drawable.mushroom_b)
            //imageView.setImageResource(item.imageID)
        }
    }

}