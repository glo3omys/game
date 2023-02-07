package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityFindNumberBinding
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class FindNumberActivity : AppCompatActivity() {
    lateinit var findNumberAdapter: FindNumberAdapter

    private var mBinding: ActivityFindNumberBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    lateinit var mToast: Toast
    lateinit var customToastLayout: View

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0
    var nextNumber = 1

    val SPAN_COUNT = 4

    val gameName = "FindNumber"

    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_number)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityFindNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findNumberAdapter = FindNumberAdapter(this)

        val secTextView = binding.layTime.tvTime

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvFindnum.layoutManager = gridLayoutManager

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
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
                Toast.makeText(this@FindNumberActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                setDatas()
                setRadioState(false, binding.layBottom.radioGroup)
                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                binding.rvFindnum.visibility = View.VISIBLE
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

        mToast = createToast()
        initRecycler()
        runTimer()
    }

    fun popNumber(number: Int): Boolean {
        if (number == nextNumber) {
            score += nextNumber
            nextNumber++
            binding.tvScoreFindnum.text = score.toString()
            return true
        }
        else
            toastWrong()
        return false
    }

    private fun createToast(): Toast {
        customToastLayout = layoutInflater.inflate(R.layout.score_custom_toast,null)
        return Toast(this).apply {
            setGravity(Gravity.CENTER, 0, 0)
            duration = Toast.LENGTH_SHORT
            view = customToastLayout
        }
    }
    private fun toastWrong() {
        if (mToast != null) {
            mToast.cancel()
            mToast = createToast()
        }
        val tstScore = customToastLayout.findViewById<TextView>(R.id.tv_score_toast)
        tstScore.text = "X"
        tstScore.setTextColor(Color.RED)
        tstScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 300F) // check: params
        mToast.show()
    }

    private fun pauseTimer() {
        var pauseBtn = binding.btnPause
        /*if (pauseBtn.text == "PAUSE") {
            binding.rvFindnum.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            binding.rvFindnum.visibility = View.VISIBLE
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
                    updateMyBestScore(this@FindNumberActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@FindNumberActivity)
                    mDialog.myDig("Score", score)

                    timerTask?.cancel()
                    init()
                }
            }
        }
    }

    private fun initRecycler() {
        init()
        setDatas()
        binding.rvFindnum.adapter = findNumberAdapter
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
    }
    private fun setDatas() {
        val tmpDatas = mutableListOf<FindNumberData>()
        tmpDatas.apply {
            for (i in (1 .. 20))
                add(FindNumberData(num = i, selected = false))
            shuffle()
            findNumberAdapter.datas = tmpDatas
            findNumberAdapter.notifyDataSetChanged()
        }
    }

    fun init() {
        //time = 0
        score = 0
        nextNumber = 1
        isOver = false
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreFindnum.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        //setRadioState(true, binding.layBottom.radioGroup)
        binding.layTime.pgBar.max = time
        binding.rvFindnum.visibility = View.VISIBLE
        timerTask?.cancel()
    }
}