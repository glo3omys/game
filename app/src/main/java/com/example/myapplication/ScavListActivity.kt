package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.databinding.ActivityScavListBinding
import scavDatas

class ScavListActivity : AppCompatActivity() {
    lateinit var scavAdapter: ScavAdapter
    //val datas = mutableListOf<ScavData>()

    private var mBinding: ActivityScavListBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scav_list)

        mBinding = ActivityScavListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        scavAdapter = ScavAdapter(this)

        var mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)
            //.setView(mDialogView)
            //.setTitle("ADD ITEM")

        initRecycler()
        binding.btnGotoBack.setOnClickListener() {
            val nextIntent = Intent(this, ScavengerHuntActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnAdd.setOnClickListener() {
            mBuilder.setView(mDialogView)
                .setTitle("ADD ITEM")

            mAlertDialog = mBuilder.show()
            val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
            okButton.setOnClickListener {
                val name = mDialogView.findViewById<EditText>(R.id.et_name).text.toString()
                val score = mDialogView.findViewById<EditText>(R.id.et_score).text.toString().toInt()
                val data = ScavData(name, score, false)
                //datas.add(data)
                scavDatas.add(data)
                //profileAdapter.notifyItemInserted(datas.size)

                //datas.sortBy { it.score }
                scavDatas.sortBy { it.score }
                scavAdapter.notifyDataSetChanged()
                mAlertDialog.dismiss()
            }
            val cancelButton = mDialogView.findViewById<Button>(R.id.btn_can)
            cancelButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
        binding.btnSelectAll.setOnClickListener() {
            selectAllItem()
        }
        binding.btnMultidel.setOnClickListener() {
            //for ( (index, data) in datas.withIndex().reversed() ) {
            for ( (index, data) in scavDatas.withIndex().reversed() ) {
                val data: ScavData = data
                if(data.selected) {
                    scavDatas.removeAt(index)
                }
            }
            scavAdapter.notifyDataSetChanged()
        }
    }

    private fun selectAllItem() {
        //for ( (index, data) in datas.withIndex().reversed() ) {
        for ( (index, data) in scavDatas.withIndex().reversed() ) {
            val data: ScavData = data
            data.selected = !data.selected
        }
        scavAdapter.notifyDataSetChanged()
    }

    private fun initRecycler() {
        // divider(line)
        binding.rvScavlist.apply { addItemDecoration(DividerItemDecoration(context,LinearLayout.VERTICAL)) }
        binding.rvScavlist.adapter = scavAdapter

        scavDatas.apply {
            /*
            add(ScavData(name = "paper", score = 10, false))
            add(ScavData(name = "pen", score = 20, false))
            add(ScavData(name = "book", score = 30, false))
            add(ScavData(name = "glasses", score = 40, false))
            add(ScavData(name = "coin", score = 50, false))
            */

            //datas.sortBy { it.score }

            //scavAdapter.datas = datas
            scavAdapter.datas = scavDatas
            scavAdapter.notifyDataSetChanged()
        }
    }
}