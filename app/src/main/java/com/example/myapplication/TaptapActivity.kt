package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
import android.app.AlertDialog
import androidx.core.view.isVisible
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityTaptapBinding
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

class TaptapActivity : AppCompatActivity() {
    private var mBinding: ActivityTaptapBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog

    var timerTask: Timer?= null
    var time = 0
    var cnt = 0
    var isOver = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taptap)

        mBinding = ActivityTaptapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countTextView = binding.tvCnt
        val secTextView = binding.tvTime

        var mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
            .setTitle("Score")
        mAlertDialog =  mBuilder.create()
        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnCnt.setOnClickListener {
            cnt += 1
            countTextView.text = cnt.toString()
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
                Toast.makeText(this@TaptapActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                time *= 100
                binding.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.btnStart.isEnabled = false
                //startTimer(mDialogView, mBuilder)
                runTimer(mDialogView, mBuilder)
            }
        }
        binding.btnReset.setOnClickListener() {
            cnt = 0
            time = 0
            stopTimer()
        }
        binding.btnPause.setOnClickListener {
            pauseTimer(mDialogView, mBuilder)
        }
    }

    private fun pauseTimer(mDialogView: View, mBuilder: AlertDialog.Builder) {
        var pauseBtn = binding.btnPause
        if (pauseBtn.text == "PAUSE") {
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
            binding.btnCnt.isEnabled = false
            //binding.cntButton.isVisible = false
        }
        else {
            //binding.cntButton.isVisible = true
            pauseBtn.text = "PAUSE"
            runTimer(mDialogView, mBuilder)
        }
    }

    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }

    fun runTimer(mDialogView: View, mBuilder: AlertDialog.Builder) {
        binding.btnCnt.isEnabled = true
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
                    mDialogView.findViewById<TextView>(R.id.tv_score).text = cnt.toString()

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

    private fun init() {
        time = 0
        cnt = 0
        isOver = false
        binding.radioGroup.clearCheck()
        binding.tvCnt.text = "0"
        binding.tvTime.text = "0초"
        binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        binding.btnCnt.isEnabled = false
        binding.btnStart.isEnabled = false
        timerTask?.cancel()
    }
}