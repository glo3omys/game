package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityMemoryCardGameBinding
import setRadioState
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer
import memoryCardGameDatas

class MemoryCardGameActivity : AppCompatActivity() {
    lateinit var memoryCardGameAdapter: MemoryCardGameAdapter

    private var mBinding: ActivityMemoryCardGameBinding? = null
    private val binding get() = mBinding!!

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0

    var flippedCnt = 0
    var leftCardCnt = 12
    var flippedCards = mutableListOf<Int>()
    val SPAN_COUNT = 4

    val gameName = "MemoryCardGame"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_card_game)

        mBinding = ActivityMemoryCardGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        memoryCardGameAdapter = MemoryCardGameAdapter(this)

        val secTextView = binding.layTime.tvTime

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvMemoryCardGame.layoutManager = gridLayoutManager

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        /*binding.layBottom.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
            if (binding.layBottom.radioGroup.checkedRadioButtonId != -1)
                binding.layBottom.btnStart.isEnabled = true
        }
        binding.layBottom.btnStart.setOnClickListener {
            if (binding.layBottom.radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                setDatas()
                setRadioState(false, binding.layBottom.radioGroup)
                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                binding.rvMemoryCardGame.visibility = View.VISIBLE
                runTimer()
            }
        }
        binding.layBottom.btnReset.setOnClickListener() {
            score = 0
            time = 0
            stopTimer()
        }

         */
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }
        initRecycler()
        runTimer()
    }
    private fun pauseTimer() {
        var pauseBtn = binding.btnPause
        /*if (pauseBtn.text == "PAUSE") {
            binding.rvMemoryCardGame.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            binding.rvMemoryCardGame.visibility = View.VISIBLE
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
            }
            if (time <= 0 && !isOver) {
                isOver = true
                runOnUiThread {
                    updateMyBestScore(gameName, score.toString())
                    binding.tvBestScore.text =
                        "최고기록: " + MainActivity.prefs.getSharedPrefs(gameName, score.toString())
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@MemoryCardGameActivity)
                    mDialog.myDig("Score", score)

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
        binding.tvBestScore.text = "최고기록: " + MainActivity.prefs.getSharedPrefs(gameName, "0")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDatas() {
        val tmpDatas = mutableListOf<MemoryCardGameData>()
        val tmpDefault = mutableListOf<MemoryCardGameData>()
        tmpDefault.addAll(memoryCardGameDatas.shuffled())

        tmpDatas.apply {
            for (i in (0 until 6))
                for (j in (0..1))
                    add(MemoryCardGameData(name = tmpDefault[i].name, imageID = tmpDefault[i].imageID, selected = false, invisible = false))
            shuffle()
            memoryCardGameAdapter.datas = tmpDatas
        }

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
    private fun initDefaultCards() {
        for (card in memoryCardGameDatas) {
            card.invisible = false
            card.selected = false
        }
    }
    fun init() {
        //time = 0
        score = 0
        isOver = false
        leftCardCnt = 12
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreMemory.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = "최고기록: " + MainActivity.prefs.getSharedPrefs(gameName, "0")
        //setRadioState(true, binding.layBottom.radioGroup)
        initDefaultCards()
        binding.layTime.pgBar.max = time
        binding.rvMemoryCardGame.visibility = View.VISIBLE
        timerTask?.cancel()
    }
}