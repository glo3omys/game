package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taptap)

        mBinding = ActivityTaptapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countTextView = binding.cntTv
        val secTextView = binding.timeTv

        var mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)

        binding.cntButton.setOnClickListener {
            cnt += 1
            countTextView.text = cnt.toString()
        }
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
        }
        binding.startBtn.setOnClickListener {
            if (binding.radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this@TaptapActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else
                startTimer(mDialogView, mBuilder)
        }
        binding.rstBtn.setOnClickListener() {
            stopTimer()
        }

    }

    private fun stopTimer() {
        val countTextView = binding.cntTv
        val secTextView = binding.timeTv
        val progressBar = binding.pgBar

        cnt = 0
        time = 0

        //countTextView.setText("0")
        //secTextView.setText("0초")
        timerTask?.cancel()
        binding.radioGroup.clearCheck()
        countTextView.text = "0"
        secTextView.text = "0초"
    }

    private fun startTimer(mDialogView: View, mBuilder: AlertDialog.Builder) {
        val countTextView = binding.cntTv
        val secTextView = binding.timeTv
        val progressBar = binding.pgBar

        countTextView.text = "0"
        //time = binding.radioGroup.checkedRadioButtonId
        time *= 100
        progressBar.max = time

        timerTask = timer(period = 10) { // 10ms 마다 반복
            time--
            val sec = time / 100
            runOnUiThread {
                //Toast.makeText(this@TaptapActivity, "TOAST", Toast.LENGTH_SHORT).show()

                secTextView.text = "$sec" + "초"
                progressBar.progress = time
            }
            if (time <= 0) {
                runOnUiThread {
                    secTextView.text = "0초"
                    timerTask?.cancel()
                    time = 0
                    binding.radioGroup.clearCheck()

                    mDialogView.findViewById<TextView>(R.id.tv_score).setText(cnt.toString())
                    mBuilder.setView(mDialogView)
                        .setTitle("Score")

                    var mAlertDialog = mBuilder.show()
                    val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
                    okButton.setOnClickListener {
                        cnt = 0
                        mAlertDialog.dismiss()
                    }
                }
            }
        }
    }
}