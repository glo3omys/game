package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityWhackAMoleBinding
import setRadioState
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class WhackAMoleActivity : AppCompatActivity() {
    lateinit var whackAMoleAdapter: WhackAMoleAdapter
    //val datas = mutableListOf<WhackAMoleData>()
    val defaultMoles = listOf("mole", "bomb")

    private var mBinding: ActivityWhackAMoleBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog

    var timerTask: Timer?= null
    var time = 0
    var isOver = false
    val moleTimer = arrayListOf<Timer?>()
    var questTimer: Timer?= null
    var questTime = 0
    var questIsOver = false
    var questTimerStarted = false

    var score = 0

    val SPAN_COUNT = 4

    val gameName = "WhackAMole"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whack_a_mole)

        mBinding = ActivityWhackAMoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        whackAMoleAdapter = WhackAMoleAdapter(this)

        val secTextView = binding.layTime.tvTime

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvWhackAMole.layoutManager = gridLayoutManager

        var mDialogView = LayoutInflater.from(this).inflate(R.layout.result_custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
            .setTitle("Score")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        binding.layBottom.radioGroup.setOnCheckedChangeListener { group, checkedId ->
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
                Toast.makeText(this@WhackAMoleActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                setDatas()
                setRadioState(false, binding.layBottom.radioGroup)
                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                binding.rvWhackAMole.visibility = View.VISIBLE
                runTimer(mDialogView)
            }
        }
        binding.layBottom.btnReset.setOnClickListener() {
            score = 0
            time = 0
            stopTimer()
        }
        binding.btnPause.setOnClickListener {
            pauseTimer(mDialogView)
        }
        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        initRecycler()
    }
    private fun pauseTimer(mDialogView: View) {
        var pauseBtn = binding.btnPause
        if (pauseBtn.text == "PAUSE") {
            binding.rvWhackAMole.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            binding.rvWhackAMole.visibility = View.VISIBLE
            pauseBtn.text = "PAUSE"
            runTimer(mDialogView)
        }
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer(mDialogView: View) {
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
                    updateMyBestScore(gameName, score.toString())
                    binding.tvBestScore.text = MainActivity.prefs.getSharedPrefs(gameName, score.toString())
                    secTextView.text = "0초"
                    mDialogView.findViewById<TextView>(R.id.tv_custom_result).text = score.toString()

                    mAlertDialog.show()
                    val okButton = mDialogView.findViewById<Button>(R.id.btn_con)
                    okButton.setOnClickListener {
                        init()
                        mAlertDialog.dismiss()
                    }
                    timerTask?.cancel()
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

        // mole or bomb
        whackAMoleAdapter.datas[nextMole].apply {
            selected = true
            if ((0 until 100).random() < 80) {
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

        // mole timer 1sec
        var moleTime = 100
        var moleIsOver = false

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
        binding.tvBestScore.text = MainActivity.prefs.getSharedPrefs(gameName, "0")
    }
    private fun setDatas() {
        val tmpDatas = mutableListOf<WhackAMoleData>()
        tmpDatas.apply {
            for (i in (0 until SPAN_COUNT * SPAN_COUNT))
                add(WhackAMoleData(name = "mole", imageID = R.drawable.mokoko, selected = false))
            whackAMoleAdapter.datas = tmpDatas
            whackAMoleAdapter.notifyDataSetChanged()
        }
    }

    fun init() {
        time = 0
        score = 0
        isOver = false
        binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreWam.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        binding.rvWhackAMole.visibility = View.GONE
        binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = MainActivity.prefs.getSharedPrefs(gameName, "0")
        setRadioState(true, binding.layBottom.radioGroup)
        timerTask?.cancel()
    }

    fun popMole(isMole: Boolean, position: Int) {
        if (isMole)
            score++
        else
            score--

        whackAMoleAdapter.datas[position].selected = false
        runOnUiThread {
            binding.tvScoreWam.text = score.toString()
            whackAMoleAdapter.notifyItemChanged(position)
        }
    }
}