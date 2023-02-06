package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.example.myapplication.databinding.ActivityFingerChoiceBinding
import java.util.*
import kotlin.concurrent.timer
import kotlin.random.Random

class FingerChoiceActivity : AppCompatActivity() {
    private var mBinding: ActivityFingerChoiceBinding? = null
    private val binding get() = mBinding!!

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var thisContext: Context? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_finger_choice)

        mBinding = ActivityFingerChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        thisContext = this

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            startActivity(nextIntent)
        }
        binding.cvFinger.setOnTouchListener(View.OnTouchListener { v, event ->
            val eventID = event.getPointerId((event.actionIndex))
            when (event.actionMasked) {
                /*MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    binding.cvFinger.rmData(eventID)
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.cvFinger.modData(eventID, event.getX(eventID), event.getY(eventID))
                }*/
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    //binding.cvFinger.addData(eventID, FingerChoiceData(ID = eventID, dX = event.getX(eventID), dY = event.getY(eventID), color = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), removed = false))
                    binding.cvFinger.addData(eventID, FingerChoiceData(ID = eventID, dX = event.getX(eventID), dY = event.getY(eventID), color = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), removed = false), binding.layTouch)
                }
                else -> {
                    v.onTouchEvent(event)
                }
            }
            v.invalidate()
            true
        })
    }

    private fun runTimer(event: MotionEvent) {
        if (timerTask != null)
            timerTask?.cancel()
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
                    /*
                    mDialogView.findViewById<TextView>(R.id.tv_custom_result).text = "Idx: ${selectedIdx}, Id: ${selectedID}"
                    mAlertDialog.show()
                    val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
                    okButton.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                     */
                    timerTask?.cancel()
                }
            }
        }

    }

}