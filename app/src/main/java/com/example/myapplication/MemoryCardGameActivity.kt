package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityMemoryCardGameBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer
import memoryCardGameDatas

class MemoryCardGameActivity : AppCompatActivity() {
    lateinit var memoryCardGameAdapter: MemoryCardGameAdapter

    private var mBinding: ActivityMemoryCardGameBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    var timerTask: Timer?= null
    var time = 0
    var isOver = false
    var score = 0
    var paused = false
    var flippedCnt = 0
    var leftCardCnt = 12
    var flippedCards = mutableListOf<Int>()
    val SPAN_COUNT = 4
    var gameStart = false
    val gameName = "MemoryCardGame"

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""
    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    var gameData = mutableListOf<MemoryCardGameData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_card_game)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityMemoryCardGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        memoryCardGameAdapter = MemoryCardGameAdapter(this)

        val secTextView = binding.layTime.tvTime

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvMemoryCardGame.layoutManager = gridLayoutManager

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */
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
            timerTask?.cancel()
            val nextIntent = Intent(this, GameListActivity::class.java)
            this@MemoryCardGameActivity.finish()
            startActivity(nextIntent)
        }
        binding.layMenu.btnPause.setOnClickListener {
            pauseTimer()
        }
        initRecycler()
    }
    private fun pauseTimer() {
        if (!paused) {
            paused = true
            binding.rvMemoryCardGame.visibility = View.GONE
            timerTask?.cancel()
        }
        else {
            paused = false
            binding.rvMemoryCardGame.visibility = View.VISIBLE
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
                secTextView.text = "$sec" + "초"
                progressBar.progress = time
            }
            if (time <= 0 && !isOver) {
                isOver = true
                runOnUiThread {
                    updateMyBestScore(this@MemoryCardGameActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@MemoryCardGameActivity)
                    if (roomPk.isEmpty()) {
                        mDialog.myDig("Score", score)
                    }
                    else {
                        myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)
                        mDialog.myDig("Rank", intent.getSerializableExtra("roomInfoData") as RoomInfoData)
                    }
                    timerTask?.cancel()
                    //init()
                }
            }
        }
    }
    private fun initRecycler() {
        init()
        setDatas()
        binding.rvMemoryCardGame.adapter = memoryCardGameAdapter
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
    }

    private fun setDatas() {
        if (masterName == myID && !gameStart)
            myRoomRef.child("gameInfo").child("gameData").setValue("0")
        val tmpDatas = mutableListOf<MemoryCardGameData>()
        val tmpDefault = mutableListOf<MemoryCardGameData>()
        tmpDefault.addAll(memoryCardGameDatas.shuffled())

        for (i in 0 until 6)
            for (j in 0 .. 1)
                tmpDatas.add(MemoryCardGameData(name = tmpDefault[i].name, imageID = tmpDefault[i].imageID, selected = false, invisible = false))
        tmpDatas.shuffle()
        memoryCardGameAdapter.datas = tmpDatas

        runOnUiThread() {
            memoryCardGameAdapter.notifyDataSetChanged()
        }
    }
    fun flipCard(position: Int) {
        if (memoryCardGameAdapter.datas[position].selected || flippedCnt == 2)
            return
        memoryCardGameAdapter.datas[position].selected = true
        memoryCardGameAdapter.notifyItemChanged(position)
        flippedCnt++
        flippedCards.add(position)
        if (flippedCnt == 2) {
            Handler(Looper.getMainLooper()).postDelayed({
                //실행할 코드
                popCard()
            }, 500)
        }
    }
    private fun popCard() {
        memoryCardGameAdapter.datas.run {
            for (i in flippedCards)
                this[i].selected = false
            if (this[flippedCards[0]].name == this[flippedCards[1]].name) {
                score++
                leftCardCnt -= 2
                for (i in flippedCards)
                    this[i].invisible = true
                binding.tvScoreMemory.text = score.toString()
            }
        }
        runOnUiThread() {
            memoryCardGameAdapter.notifyDataSetChanged()
        }
        flippedCards.clear()
        flippedCnt = 0

        if (leftCardCnt == 0) {
            setDatas()
            leftCardCnt = 12
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun initDefaultCards() {
        for (card in memoryCardGameDatas) {
            card.invisible = false
            card.selected = false
        }
    }
    fun init() {
        score = 0
        isOver = false
        leftCardCnt = 12
        binding.tvScoreMemory.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        initDefaultCards()
        binding.layTime.pgBar.max = time
        //binding.rvMemoryCardGame.visibility = View.VISIBLE
        timerTask?.cancel()

        val mDialog = CountDownDialog(this@MemoryCardGameActivity)
        mDialog.countDown()
        Handler().postDelayed({
            binding.rvMemoryCardGame.visibility = View.VISIBLE
            runTimer()
        }, 3100)
    }
}