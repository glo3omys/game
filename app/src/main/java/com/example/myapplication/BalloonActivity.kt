package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.MainActivity.Companion.prefs
import com.example.myapplication.databinding.ActivityBalloonBinding
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class BalloonActivity: AppCompatActivity() {
    lateinit var balloonAdapter: BalloonAdapter
    val datas = mutableListOf<BalloonData>()
    val defaultBalloons = listOf("RED", "ORANGE", "YELLOW", "GREEN", "BLUE")

    private var mBinding: ActivityBalloonBinding? = null
    private val binding get() = mBinding!!

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0
    var leftBalloonCnt = 2

    val SPAN_COUNT = 4

    val gameName = "Balloon"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balloon)

        mBinding = ActivityBalloonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        balloonAdapter = BalloonAdapter(this)

        val countTextView = binding.tvScoreBalloon
        val secTextView = binding.layTime.tvTime

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvBalloon.layoutManager = gridLayoutManager

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        /*binding.layBottom.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
            if (binding.layBottom.radioGroup.checkedRadioButtonId != -1)
                binding.layBottom.btnStart.isEnabled = true
            //setRadioStyle(group)
        }
        binding.layBottom.btnStart.setOnClickListener {
            if (binding.layBottom.radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this@BalloonActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                allocProb()
                setRadioState(false, binding.layBottom.radioGroup)
                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                binding.rvBalloon.visibility = View.VISIBLE
                runTimer()
            }
        }
        binding.layBottom.btnReset.setOnClickListener() {
            score = 0
            time = 0
            stopTimer()
        }

         */
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }
        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }

        initRecycler()

        allocProb()
        runTimer()
    }

    private fun pauseTimer() {
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
            runTimer()
        }
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer() {
        //binding.btnCnt.isEnabled = true
        val secTextView = binding.layTime.tvTime
        val progressBar = binding.layTime.pgBar

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
                    updateMyBestScore(gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, score.toString())
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@BalloonActivity)
                    mDialog.myDig("Score", score)

                    timerTask?.cancel()
                }
            }
        }
    }

    private fun initRecycler() {
        binding.rvBalloon.adapter = balloonAdapter
        binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, "0")

        datas.apply {
            val range = (0..SPAN_COUNT)
            for (i in 0 until SPAN_COUNT * SPAN_COUNT)
               add(BalloonData(name = defaultBalloons[range.random()]))
            balloonAdapter.datas = datas
            balloonAdapter.notifyDataSetChanged()
        }
        //setRadioState(true, binding.layBottom.radioGroup)
        init()
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
        binding.tvScoreBalloon.text = score.toString()
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
        //time = 0
        score = 0
        isOver = false
        leftBalloonCnt = 2
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreBalloon.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.btnPause.text = "PAUSE"
        binding.tvBalloon1.text = "..."
        binding.tvBalloon2.text = "..."
        binding.btnPause.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, "0")
        //setRadioState(true, binding.layBottom.radioGroup)
        binding.layTime.pgBar.max = time
        binding.rvBalloon.visibility = View.VISIBLE
        timerTask?.cancel()
    }
}