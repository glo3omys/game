package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    lateinit var mAlertDialog: AlertDialog
    //var mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
    //var mBuilder = AlertDialog.Builder(this)
    var binding : ActivityMainBinding? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val countButton = findViewById<Button>(R.id.cnt_button)
        val startButton = findViewById<Button>(R.id.startBtn)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val secTextView = findViewById<TextView>(R.id.time_tv)
        val countTextView = findViewById<TextView>(R.id.cnt_tv)
        val progressBar = findViewById<ProgressBar>(R.id.pg_bar)
        var timerTask: Timer?= null
        var time = 0

        var mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        //var mBuilder = AlertDialog.Builder(this)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("SCORE")

        countButton.setOnClickListener {
            var cnt = countTextView.text.toString().toInt()
            cnt += 1
            countTextView.text = cnt.toString()
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
        }
        startButton.setOnClickListener {
            countTextView.text = "0"
            time *= 100
            progressBar.max = time
            timerTask = timer(period = 10) {
                time--
                val sec = time / 100
                runOnUiThread {
                    secTextView.text = "$sec" + "초"
                    progressBar.progress = time
                }
                if (time == 0) {
                    secTextView.text = "0"
                    timerTask?.cancel()

                    //alertScore()
                }
            }
        }
    }

    /*private fun alertScore() {
        mAlertDialog = mBuilder.show()
        val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
        okButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }*/
}