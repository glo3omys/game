package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapplication.databinding.ActivityScavengerHuntBinding
import scavDatas

class ScavengerHuntActivity : AppCompatActivity() {
    val datas = mutableListOf<ScavData>()

    private var mBinding: ActivityScavengerHuntBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog

    val gameName = "ScavengerHunt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scavenger_hunt)

        mBinding = ActivityScavengerHuntBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnItemlist.setOnClickListener() {
            val nextIntent = Intent(this, ScavListActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnStart.setOnClickListener() {
            val range = (0 until scavDatas.size)
            val idx = range.random()
            binding.tvItemName.text = scavDatas[idx].name
            binding.tvItemScore.text = scavDatas[idx].score.toString()
        }
        binding.layMenu.btnPause.visibility = View.GONE
        binding.layMenu.btnLayQuit.setOnClickListener() {
            val nextIntent = Intent(this, GameListActivity::class.java)
            this@ScavengerHuntActivity.finish()
            startActivity(nextIntent)
        }
    }
}