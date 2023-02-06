package com.example.myapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchRoomAdapter(private val context: SearchRoomActivity) :
    RecyclerView.Adapter<SearchRoomAdapter.ViewHolder>()
{
    var datas = mutableListOf<RoomData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_room,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)

        holder.itemView.setOnClickListener() {
            // pop up: room info
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_rv_roomTitle)
        private val enterBtn: Button = itemView.findViewById(R.id.btn_rv_roomEnter)

        fun bind(item: RoomData, position: Int) {
            tvTitle.text = item.title
            enterBtn.setOnClickListener() {
                context.searchRoomBySeed(datas[position].seed)
            }
        }
    }
}