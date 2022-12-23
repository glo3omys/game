package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityBalloonBinding

class BalloonActivity: AppCompatActivity() {
    lateinit var balloonAdapter: BalloonAdapter
    val datas = mutableListOf<BalloonData>()

    private var mBinding: ActivityBalloonBinding? = null
    private val binding get() = mBinding!!
    val SPAN_COUNT = 4
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balloon)

        mBinding = ActivityBalloonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        balloonAdapter = BalloonAdapter(this)

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvBalloon.layoutManager = gridLayoutManager

        initRecycler()

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
    }

    private fun initRecycler() {
        binding.rvBalloon.adapter = balloonAdapter

        datas.apply {
            val range = (1..5)
            for (i in 1..16) {
               add(BalloonData(name = range.random())) }
            balloonAdapter.datas = datas
            balloonAdapter.notifyDataSetChanged()
        }
    }
}