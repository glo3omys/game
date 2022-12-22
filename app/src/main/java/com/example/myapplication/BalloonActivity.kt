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
        val range = (1..5)
        for (i in 1..16) {
            datas.apply { add(BalloonData(name = range.random())) }
        }
        datas.apply {
            balloonAdapter.datas = datas
            balloonAdapter.notifyDataSetChanged()
        }
        /*datas.apply {
            add(BalloonData(name = 1))
            add(BalloonData(name = 2))
            add(BalloonData(name = 3))
            add(BalloonData(name = 4))
            add(BalloonData(name = 5))
            add(BalloonData(name = 6))
            add(BalloonData(name = 7))
            add(BalloonData(name = 8))
            add(BalloonData(name = 9))
            add(BalloonData(name = 10))
            add(BalloonData(name = 11))
            add(BalloonData(name = 12))
            add(BalloonData(name = 13))
            add(BalloonData(name = 14))
            add(BalloonData(name = 15))
            add(BalloonData(name = 16))

            balloonAdapter.datas = datas
            balloonAdapter.notifyDataSetChanged()
        }

         */
    }
}