package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityBalloonBinding
import java.util.*
import kotlin.concurrent.timer

class BalloonActivity: AppCompatActivity() {
    lateinit var balloonAdapter: BalloonAdapter
    val datas = mutableListOf<BalloonData>()
    val defaultBalloons = listOf("RED", "ORANGE", "YELLOW", "GREEN", "BLUE")

    private var mBinding: ActivityBalloonBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0
    var leftBalloonCnt = 2

    val SPAN_COUNT = 4

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balloon)

        mBinding = ActivityBalloonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        balloonAdapter = BalloonAdapter(this)

        val countTextView = binding.tvCnt
        val secTextView = binding.tvTime

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvBalloon.layoutManager = gridLayoutManager

        var mDialogView = LayoutInflater.from(this).inflate(R.layout.score_custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
            .setTitle("Score")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
            if (binding.radioGroup.checkedRadioButtonId != -1)
                binding.btnStart.isEnabled = true
        }
        binding.btnStart.setOnClickListener {
            if (binding.radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this@BalloonActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                allocProb()
                time *= 100
                binding.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.btnStart.isEnabled = false
                binding.rvBalloon.visibility = View.VISIBLE
                runTimer(mDialogView)
            }
        }
        binding.btnReset.setOnClickListener() {
            score = 0
            time = 0
            stopTimer()
        }
        binding.btnPause.setOnClickListener {
            pauseTimer(mDialogView)
        }
        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }

        initRecycler()
    }

    private fun pauseTimer(mDialogView: View) {
        var pauseBtn = binding.btnPause
        if (pauseBtn.text == "PAUSE") {
            binding.rvBalloon.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            //binding.cntButton.isVisible = true
            binding.rvBalloon.visibility = View.VISIBLE
            pauseBtn.text = "PAUSE"
            runTimer(mDialogView)
        }
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer(mDialogView: View) {
        //binding.btnCnt.isEnabled = true
        val secTextView = binding.tvTime
        val progressBar = binding.pgBar

        timerTask = timer(period = 10) { // 10ms 마다 반복
            time--
            val sec = time / 100
            runOnUiThread {
                //Toast.makeText(this@TaptapActivity, "TOAST", Toast.LENGTH_SHORT).show()

                secTextView.text = "$sec" + "초"
                progressBar.progress = time
            }
            if (time <= 0 && !isOver) {
                isOver = true
                runOnUiThread {
                    //Toast.makeText(this@TaptapActivity, "TOAST", Toast.LENGTH_SHORT).show()
                    secTextView.text = "0초"
                    mDialogView.findViewById<TextView>(R.id.tv_score).text = score.toString()

                    mAlertDialog.show()
                    val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
                    okButton.setOnClickListener {
                        init()
                        mAlertDialog.dismiss()
                    }
                    timerTask?.cancel()
                }
            }
        }
    }

    private fun initRecycler() {
        binding.rvBalloon.adapter = balloonAdapter

        datas.apply {
            val range = (0..SPAN_COUNT)
            for (i in 0 until SPAN_COUNT * SPAN_COUNT) {
               add(BalloonData(name = defaultBalloons[range.random()])) }
            balloonAdapter.datas = datas
            balloonAdapter.notifyDataSetChanged()
        }
    }

    private fun allocQuest() {
        var range = (0 until SPAN_COUNT * SPAN_COUNT)
        val balloon1 = binding.tvBalloon1
        val balloon2 = binding.tvBalloon2
        val rdmIdx1 = range.random()
        Toast.makeText(this, datas[rdmIdx1].name, Toast.LENGTH_SHORT).show()

        var rdmIdx2 = range.random()
        while (rdmIdx1 == rdmIdx2)
            rdmIdx2 = range.random()

        balloon1.text = datas[rdmIdx1].name
        balloon2.text = datas[rdmIdx2].name

        range = (0..SPAN_COUNT)
        balloon1.setTextColor(Color.parseColor(BalloonColors.valueOf(defaultBalloons[range.random()]).RGB.toString()))
        balloon2.setTextColor(Color.parseColor(BalloonColors.valueOf(defaultBalloons[range.random()]).RGB.toString()))
    }
    fun popBalloon(colorName: String) {
        val balloon1: TextView = binding.tvBalloon1
        val balloon2: TextView = binding.tvBalloon2

        if (balloon1.text == colorName)
            balloon1.text = "..."
        else if (balloon2.text == colorName)
            balloon2.text = "..."
        else
            return

        score++
        binding.tvCnt.text = score.toString()
        leftBalloonCnt--

        if (leftBalloonCnt == 0) {
            leftBalloonCnt = 2
            allocProb()
        }
        //Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, colorName, Toast.LENGTH_SHORT).show()
    }
    private fun allocProb() {
        val tmpDatas = mutableListOf<BalloonData>()
        tmpDatas.apply {
            val range = (0..SPAN_COUNT)
            for (i in 0 until SPAN_COUNT * SPAN_COUNT) {
                add(BalloonData(name = defaultBalloons[range.random()])) }
            balloonAdapter.datas = tmpDatas
            balloonAdapter.notifyDataSetChanged()
        }

        var range = (0 until SPAN_COUNT * SPAN_COUNT)
        val balloon1 = binding.tvBalloon1
        val balloon2 = binding.tvBalloon2
        val rdmIdx1 = range.random()
        var rdmIdx2 = range.random()
        while (rdmIdx1 == rdmIdx2)
            rdmIdx2 = range.random()

        balloon1.text = tmpDatas[rdmIdx1].name
        balloon2.text = tmpDatas[rdmIdx2].name

        range = (0..SPAN_COUNT)
        balloon1.setTextColor(Color.parseColor(BalloonColors.valueOf(defaultBalloons[range.random()]).RGB.toString()))
        balloon2.setTextColor(Color.parseColor(BalloonColors.valueOf(defaultBalloons[range.random()]).RGB.toString()))
    }

    fun init() {
        time = 0
        score = 0
        isOver = false
        leftBalloonCnt = 2
        binding.radioGroup.clearCheck()
        binding.tvCnt.text = "0"
        binding.tvTime.text = "0초"
        binding.btnPause.text = "PAUSE"
        binding.tvBalloon1.text = "..."
        binding.tvBalloon2.text = "..."
        binding.btnPause.isEnabled = false
        binding.rvBalloon.visibility = View.GONE
        binding.btnStart.isEnabled = false
        timerTask?.cancel()
    }
}