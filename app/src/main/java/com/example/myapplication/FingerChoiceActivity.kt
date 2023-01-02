package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityFingerChoiceBinding
import java.util.*
import kotlin.concurrent.timer

class FingerChoiceActivity : AppCompatActivity() {
    private var mBinding: ActivityFingerChoiceBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog
    lateinit var mDialogView: View

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger_choice)

        mBinding = ActivityFingerChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDialogView = LayoutInflater.from(this).inflate(R.layout.score_custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
            .setTitle("Score")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val tvTouch = binding.tvTouch

        val dX = event.x.toInt()
        val dY = event.y.toInt()
        tvTouch.append("x: ${dX}, y: ${dY} \n")

        when (event.actionMasked) {
            // 2개 이상 들어오면 timer run
            MotionEvent.ACTION_POINTER_DOWN -> {
                timerTask?.cancel()
                time = 300
                runTimer(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                timerTask?.cancel()
                time = 300
                runTimer(event)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun runTimer(event: MotionEvent) {
        timerTask = timer(period = 10) { // 10ms 마다 반복
            time--
            val sec = time / 100
            runOnUiThread {

            }
            if (time <= 0 && !isOver) {
                isOver = true
                val range = (0 until event.pointerCount)
                val selectedIdx = range.random()
                val selectedID = event.getPointerId(selectedIdx)

                runOnUiThread {
                    mDialogView.findViewById<TextView>(R.id.tv_score).text = "Idx: ${selectedIdx}, Id: ${selectedID}"
                    mAlertDialog.show()
                    val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
                    okButton.setOnClickListener {
                        mAlertDialog.dismiss()
                    }
                    timerTask?.cancel()
                }
            }
        }

    }

}