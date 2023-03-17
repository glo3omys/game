package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.example.myapplication.databinding.ActivityTaptapBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class TaptapActivity : AppCompatActivity() {
    private var mBinding: ActivityTaptapBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    var timerTask: Timer?= null
    var time = 0
    var score = 0
    var paused = false
    var isOver = false
    val gameName = "Taptap"

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""

    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    lateinit var gameData : String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taptap)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityTaptapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_blink)
        binding.tvClick.startAnimation(anim)

        val countTextView = binding.tvScoreTaptap

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */
        val roomInfoData = intent.getSerializableExtra("roomInfoData") as? RoomInfoData
        if (roomInfoData != null) {
            roomPk = roomInfoData.roomPk
            myPk = roomInfoData.myPk
            masterName = roomInfoData.masterName
        }
        myRoomRef = database.getReference("room").child(roomPk)
        myID = prefs.getSharedPrefs("myID", "")

        binding.layMenu.btnLayQuit.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            this@TaptapActivity.finish()
            startActivity(nextIntent)
        }
        binding.layMenu.btnPause.setOnClickListener {
            pauseTimer()
        }
        binding.btnTap.setOnClickListener {
            score += 1
            countTextView.text = score.toString()
        }

        init()
        //runTimer()
    }

    private fun pauseTimer() {
        if (!paused) {
            paused = true
            timerTask?.cancel()
            binding.btnTap.isEnabled = false
        }
        else {
            paused = false
            runTimer()
        }
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
                    updateMyBestScore(this@TaptapActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)

                    val mDialog = MyDialog(this@TaptapActivity)
                    if (roomPk.isEmpty())
                        mDialog.myDig("Score", score)
                    else
                        mDialog.myDig("Rank", intent.getSerializableExtra("roomInfoData") as RoomInfoData)

                    timerTask?.cancel()
                    //init()
                }
            }
        }
    }

    private fun init() {
        if (masterName == myID)
            myRoomRef.child("gameInfo").child("gameData").setValue("0")
        else if (masterName != "") {
            myRoomRef.child("gameInfo").child("gameData").get().addOnSuccessListener {
                gameData = it.value.toString()
            }
        }

        score = 0
        isOver = false
        binding.tvScoreTaptap.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.btnTap.isEnabled = false
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        binding.layTime.pgBar.max = time
        timerTask?.cancel()

        runTimer()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onDestroy() {
        removeListener()
        super.onDestroy()
    }

    private fun removeListener() {
        if (dbListener != null) {
            myRoomRef.removeEventListener(dbListener!!)
            dbListener = null
        }
    }
}