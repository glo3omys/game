package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.bumptech.glide.disklrucache.DiskLruCache.Value
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
    var cnt = 0
    var isOver = false

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""

    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    lateinit var gameData : String

    val gameName = "Taptap"

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
        /*masterName = intent.getStringExtra("masterName").toString()
        roomPk = intent.getStringExtra("roomPk").toString()
        myPk = intent.getStringExtra("myPk").toString()*/
        myRoomRef = database.getReference("room").child(roomPk)
        myID = prefs.getSharedPrefs("myID", "")

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnTap.setOnClickListener {
            cnt += 1
            countTextView.text = cnt.toString()
        }
        /*
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
        //runTimer()
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
                    updateMyBestScore(this@TaptapActivity, gameName, cnt.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, cnt.toString())}"
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@TaptapActivity)
                    mDialog.myDig("Score", cnt, intent.getSerializableExtra("roomInfoData") as RoomInfoData)

                    myRoomRef.child("gameInfo").child("gameData").removeValue()
                    myRoomRef.child("readyCnt").setValue(0)
                    myRoomRef.child("memberList").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (data in snapshot.children)
                                data.child("readyState").ref.setValue(false)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

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

        //time = 0
        cnt = 0
        isOver = false
        binding.tvScoreTaptap.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        binding.btnTap.isEnabled = false
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        binding.layTime.pgBar.max = time
        timerTask?.cancel()

        runTimer()
    }

    override fun onDestroy() {
        removeListener()
        super.onDestroy()
    }

    fun removeListener() {
        if (dbListener != null) {
            myRoomRef.removeEventListener(dbListener!!)
            dbListener = null
        }
    }
}