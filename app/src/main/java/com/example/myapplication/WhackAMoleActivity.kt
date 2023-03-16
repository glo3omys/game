package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityWhackAMoleBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class WhackAMoleActivity : AppCompatActivity() {
    lateinit var whackAMoleAdapter: WhackAMoleAdapter
    //val datas = mutableListOf<WhackAMoleData>()
    val defaultMoles = listOf("mole", "gold", "bomb")

    private var mBinding: ActivityWhackAMoleBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    var timerTask: Timer?= null
    var time = 0
    var isOver = false
    val moleTimer = arrayListOf<Timer?>()
    var questTimer: Timer?= null
    var questTime = 0
    var questIsOver = false
    var questTimerStarted = false

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""
    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    var gameData = mutableListOf<FindNumberData>()

    var score = 0

    val SPAN_COUNT = 4

    val gameName = "WhackAMole"

    //val displayMetrics = DisplayMetrics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whack_a_mole)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityWhackAMoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        whackAMoleAdapter = WhackAMoleAdapter(this)

        val secTextView = binding.layTime.tvTime

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvWhackAMole.layoutManager = gridLayoutManager

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

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }
        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            startActivity(nextIntent)
        }
        initRecycler()
        //runTimer()
    }
    private fun pauseTimer() {
        /*var pauseBtn = binding.btnPause
        if (pauseBtn.text == "PAUSE") {
            binding.rvWhackAMole.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            binding.rvWhackAMole.visibility = View.VISIBLE
            pauseBtn.text = "PAUSE"
            runTimer()
        }*/
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
                secTextView.text = "$sec" + "초"
                progressBar.progress = time

                if (!questTimerStarted)
                    allocQuestTimer()
            }
            if (time <= 0 && !isOver) {
                isOver = true
                runOnUiThread {
                    updateMyBestScore(this@WhackAMoleActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)

                    val mDialog = MyDialog(this@WhackAMoleActivity)
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
    // 두더지를 부르기 까지의 대기 시간 설정
    private fun allocQuestTimer() {
        questTimerStarted = true
        questTime = (50 .. 120).random()

        questTimer = timer(period = 10) {
            questTime--
            runOnUiThread {

            }
            if (questTime <= 0 && !questIsOver) {
                questIsOver = true
                runOnUiThread {
                    allocQuest()
                    questTimer?.cancel()
                }
            }
        }
    }

    private fun allocQuest() {
        questIsOver = false
        questTimerStarted = false

        // set next mole's index
        var nextMole = (0 until SPAN_COUNT * SPAN_COUNT).random()
        while (whackAMoleAdapter.datas[nextMole].selected)
            nextMole = (0 until SPAN_COUNT * SPAN_COUNT).random()

        var moleTime = 100
        var moleIsOver = false
        whackAMoleAdapter.datas[nextMole].apply {
            selected = true
            val p = (0 until 100).random()
            if (p < 5) {
                name = "gold"
                imageID = R.drawable.mokoko_g
                moleTime = 50
            }
            else if (p < 70) {
                name = "mole"
                imageID = R.drawable.mokoko
            }
            else {
                name = "bomb"
                imageID = R.drawable.mushroom_z
            }
            runOnUiThread {
                whackAMoleAdapter.notifyItemChanged(nextMole)
            }
        }

        moleTimer[nextMole] = timer(period = 10) {
            moleTime--
            if (moleTime <= 0 && !moleIsOver) {
                moleIsOver = true
                whackAMoleAdapter.datas[nextMole].selected = false
                runOnUiThread {
                    whackAMoleAdapter.notifyItemChanged(nextMole)
                    moleTimer[nextMole]?.cancel()
                }
            }
        }
    }

    private fun initRecycler() {
        init()
        setDatas()
        for (i in (0 until SPAN_COUNT * SPAN_COUNT))
            moleTimer.add(Timer())
        binding.rvWhackAMole.adapter = whackAMoleAdapter
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"

        runTimer()
    }
    private fun setDatas() {
        if (masterName == myID)
            myRoomRef.child("gameInfo").child("gameData").setValue("0")

        val tmpDatas = mutableListOf<WhackAMoleData>()
        tmpDatas.apply {
            for (i in (0 until SPAN_COUNT * SPAN_COUNT))
                add(WhackAMoleData(name = "mole", imageID = R.drawable.mokoko, selected = false))
            whackAMoleAdapter.datas = tmpDatas
            whackAMoleAdapter.notifyDataSetChanged()
        }
    }

    fun init() {
        //time = 0
        score = 0
        isOver = false
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreWam.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        //setRadioState(true, binding.layBottom.radioGroup)
        binding.layTime.pgBar.max = time
        binding.rvWhackAMole.visibility = View.VISIBLE
        timerTask?.cancel()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    fun popMole(moleScore: Int, position: Int) {
        score += moleScore

        whackAMoleAdapter.datas[position].selected = false
        runOnUiThread {
            binding.tvScoreWam.text = score.toString()
            whackAMoleAdapter.notifyItemChanged(position)
        }
    }
}