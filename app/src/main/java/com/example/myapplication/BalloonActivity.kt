package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityBalloonBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class BalloonActivity: AppCompatActivity() {
    lateinit var balloonAdapter: BalloonAdapter
    val datas = mutableListOf<BalloonData>()
    val defaultBalloons = listOf("RED", "ORANGE", "YELLOW", "GREEN", "BLUE")

    private var mBinding: ActivityBalloonBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    var timerTask: Timer?= null
    var time = 0
    var isOver = false
    var score = 0
    var paused = false
    var leftBalloonCnt = 2
    val SPAN_COUNT = 4
    val gameName = "Balloon"

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""
    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    var gameData = mutableListOf<FindNumberData>()
    private var myRandom = Random(1)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balloon)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityBalloonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        balloonAdapter = BalloonAdapter(this)

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvBalloon.layoutManager = gridLayoutManager

        val intent = intent
        time = intent.getIntExtra("time", 0)
        val roomInfoData = intent.getSerializableExtra("roomInfoData") as? RoomInfoData
        if (roomInfoData != null) {
            roomPk = roomInfoData.roomPk
            myPk = roomInfoData.myPk
            masterName = roomInfoData.masterName
            binding.layMenu.root.visibility = View.GONE
        }
        else
            binding.layMenu.root.visibility = View.VISIBLE
        myRoomRef = database.getReference("room").child(roomPk)
        myID = prefs.getSharedPrefs("myID", "")

        binding.layMenu.btnLayQuit.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            this@BalloonActivity.finish()
            startActivity(nextIntent)
        }
        binding.layMenu.btnPause.setOnClickListener {
            pauseTimer()
        }

        initRecycler()

        allocProb()
        runTimer()
    }

    private fun pauseTimer() {
        if (!paused) {
            paused = true
            binding.layBalloonQuest.visibility = View.INVISIBLE
            binding.rvBalloon.visibility = View.INVISIBLE
            timerTask?.cancel()
        }
        else {
            paused = false
            binding.layBalloonQuest.visibility = View.VISIBLE
            binding.rvBalloon.visibility = View.VISIBLE
            runTimer()
        }
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer() {
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
                    updateMyBestScore(this@BalloonActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)

                    val mDialog = MyDialog(this@BalloonActivity)
                    if (roomPk.isEmpty())
                        mDialog.myDig("Score", score)
                    else
                        mDialog.myDig("Rank", intent.getSerializableExtra("roomInfoData") as RoomInfoData)

                    timerTask?.cancel()
                    binding.layBalloonQuest.visibility = View.INVISIBLE
                    binding.rvBalloon.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initRecycler() {
        binding.rvBalloon.adapter = balloonAdapter
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"

        if (masterName == myID || masterName == "") {
            // generate
            val seedValue = System.currentTimeMillis()
            myRandom.setSeed(1)
            // upload
            if (masterName == myID)
                myRoomRef.child("gameInfo").child("gameData").setValue(seedValue)
            allocProb()
        }
        else {
            // download
            myRoomRef.child("gameInfo").child("gameData").get().addOnSuccessListener {
                //myRandom = Random(it.value.toString().toLong())
                val seedValue = it.value.toString().toLong()
                myRandom.setSeed(1)
                allocProb()
            }
        }
        init()
    }

    fun popBalloon(colorName: String) {
        val balloon1: TextView = binding.tvBalloon1
        val balloon2: TextView = binding.tvBalloon2

        if (balloon1.text == colorName) {
            balloon1.text = "..."
            balloon1.visibility = View.INVISIBLE
        }
        else if (balloon2.text == colorName) {
            balloon2.text = "..."
            balloon2.visibility = View.INVISIBLE
        }
        else
            return

        score++
        binding.tvScoreBalloon.text = score.toString()
        leftBalloonCnt--

        if (leftBalloonCnt == 0) {
            leftBalloonCnt = 2
            allocProb()
        }
    }
    private fun allocProb() {
        val tmpDatas = mutableListOf<BalloonData>()
        for (i in 0 until SPAN_COUNT * SPAN_COUNT)
            tmpDatas.add(BalloonData(name = defaultBalloons[myRandom.nextInt(defaultBalloons.size)]))
        balloonAdapter.datas = tmpDatas
        balloonAdapter.notifyDataSetChanged()

        val balloon1 = binding.tvBalloon1
        val balloon2 = binding.tvBalloon2
        val rdmIdx1 = myRandom.nextInt(defaultBalloons.size)
        var rdmIdx2 = myRandom.nextInt(defaultBalloons.size)
        while (rdmIdx1 == rdmIdx2)
            rdmIdx2 = myRandom.nextInt(defaultBalloons.size)

        balloon1.text = tmpDatas[rdmIdx1].name
        balloon2.text = tmpDatas[rdmIdx2].name

        balloon1.setTextColor(Color.parseColor(BalloonColors.valueOf(defaultBalloons[myRandom.nextInt(defaultBalloons.size)]).RGB))
        balloon2.setTextColor(Color.parseColor(BalloonColors.valueOf(defaultBalloons[myRandom.nextInt(defaultBalloons.size)]).RGB))

        balloon1.visibility = View.VISIBLE
        balloon2.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    fun init() {
        score = 0
        isOver = false
        paused = false
        leftBalloonCnt = 2
        binding.tvScoreBalloon.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.tvBalloon1.text = "..."
        binding.tvBalloon2.text = "..."
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        binding.layTime.pgBar.max = time
        binding.rvBalloon.visibility = View.VISIBLE
        timerTask?.cancel()
    }
}