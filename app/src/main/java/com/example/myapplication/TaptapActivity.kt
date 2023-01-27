package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.myapplication.MainActivity.Companion.prefs
import com.example.myapplication.databinding.ActivityTaptapBinding
import setRadioState
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class TaptapActivity : AppCompatActivity() {
    private var mBinding: ActivityTaptapBinding? = null
    private val binding get() = mBinding!!

    var timerTask: Timer?= null
    var time = 0
    var cnt = 0
    var isOver = false

    val gameName = "Taptap"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taptap)

        mBinding = ActivityTaptapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_blink)
        binding.tvClick.startAnimation(anim)

        val countTextView = binding.tvScoreTaptap
        val secTextView = binding.layTime.tvTime

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnTap.setOnClickListener {
            cnt += 1
            countTextView.text = cnt.toString()
        }
        /*binding.layBottom.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
            if (binding.layBottom.radioGroup.checkedRadioButtonId != -1)
                binding.layBottom.btnStart.isEnabled = true
        }
        binding.layBottom.btnStart.setOnClickListener {
            if (binding.layBottom.radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this@TaptapActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                setRadioState(false, binding.layBottom.radioGroup)
                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                runTimer()
            }
        }
        binding.layBottom.btnReset.setOnClickListener() {
            cnt = 0
            time = 0
            stopTimer()
        }

         */
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }
        init()
        runTimer()
    }

    private fun pauseTimer() {
        var pauseBtn = binding.btnPause
        /*if (pauseBtn.text == "PAUSE") {
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
            binding.btnCnt.isEnabled = false
        }
        else {
            pauseBtn.text = "PAUSE"
            runTimer()
        }*/
    }

    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }

    fun runTimer() {
        binding.btnTap.isEnabled = true
        val secTextView = binding.layTime.tvTime
        val progressBar = binding.layTime.pgBar

        timerTask = timer(period = 10) { // 10ms 마다 반복
            time--
            val sec = time / 100
            runOnUiThread {
                secTextView.text = "$sec" + "초"
                progressBar.progress = time
            }
            if (time <= 0 && !isOver) {
                isOver = true
                runOnUiThread {
                    updateMyBestScore(gameName, cnt.toString())
                    binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, cnt.toString())
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@TaptapActivity)
                    mDialog.myDig("Score", cnt)

                    timerTask?.cancel()
                    //init()
                }
            }
        }
    }

    private fun init() {
        //time = 0
        cnt = 0
        isOver = false
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreTaptap.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        binding.btnTap.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, "0")
        //setRadioState(true, binding.layBottom.radioGroup)
        binding.layTime.pgBar.max = time
        timerTask?.cancel()
    }
}