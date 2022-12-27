package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ScavAdapter(private val context: Context) :
    RecyclerView.Adapter<ScavAdapter.ViewHolder>()
{
    var datas = mutableListOf<ScavData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_scav,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = datas.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        if(datas[position].selected)
            holder.itemView.setBackgroundColor(Color.parseColor("#DEDEDE"))
        else
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        holder.itemView.setOnClickListener() {
            datas[position].selected = !datas[position].selected
            notifyItemChanged(position)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val txtScore: TextView = itemView.findViewById(R.id.tv_item_score)
        private val delbtn: Button = itemView.findViewById(R.id.btn_del)
        private val editbtn: Button = itemView.findViewById(R.id.btn_edit)

        @SuppressLint("MissingInflatedId")
        fun bind(item: ScavData, position: Int) {
            txtName.text = item.name
            txtScore.text = item.score.toString()

            delbtn.setOnClickListener() {
                datas.removeAt(position)
                notifyDataSetChanged()
            }
            editbtn.setOnClickListener() {
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.edit_custom_dialog, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("EDIT ITEM")

                val mAlertDialog = mBuilder.show()
                val okButton = mDialogView.findViewById<Button>(R.id.btn_con)

                okButton.setOnClickListener() {
                    val name = mDialogView.findViewById<EditText>(R.id.et_name).text.toString()
                    val score = mDialogView.findViewById<EditText>(R.id.et_score).text.toString().toInt()
                    val data = ScavData(name, score, false)
                    datas[position] = data
                    datas.sortBy { it.score }
                    notifyDataSetChanged()
                    mAlertDialog.dismiss()
                }
            }
        }
    }
}