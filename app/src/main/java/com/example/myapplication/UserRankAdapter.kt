package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserRankAdapter(private val context: Context) :
    RecyclerView.Adapter<UserRankAdapter.ViewHolder>()
{
    var datas = mutableListOf<UserRankData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_user_rank,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = itemView.findViewById(R.id.rv_tv_name)
        private val txtScore: TextView = itemView.findViewById(R.id.rv_tv_score)

        fun bind(item: UserRankData, position: Int) {
            txtName.text = item.name
            txtScore.text = item.score.toString()
        }
    }
}