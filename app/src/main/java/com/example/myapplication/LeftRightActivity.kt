package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity.Companion.prefs
import com.example.myapplication.databinding.ActivityLeftRightBinding
import setRadioState
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class LeftRightActivity : AppCompatActivity() {
    lateinit var leftRightAdapter: LeftRightAdapter
    val defaultItems = listOf("LEFT", "RIGHT")

    private var mBinding: ActivityLeftRightBinding? = null
    private val binding get() = mBinding!!

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0
    val startIdx = 0
    var lastIdx = 0 // size = 7(0 .. 6)
    val gameName = "LeftRight"

    lateinit var mToast: Toast
    lateinit var customToastLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_left_right)

        mBinding = ActivityLeftRightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        leftRightAdapter = LeftRightAdapter(this)

        var linearLayoutManager = LinearLayoutManager(this)
        //linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.rvLeftright.layoutManager = linearLayoutManager

        lastIdx = leftRightAdapter.itemCount - 1

        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */

        binding.rvLeftright.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                if (position != lastIdx)
                    outRect.bottom -= 90
            }
        })

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        /*
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
                Toast.makeText(this@LeftRightActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                init()
                setDatas()
                setRadioState(false, binding.layBottom.radioGroup)
                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                binding.btnLeft.isEnabled = true
                binding.btnRight.isEnabled = true
                binding.rvLeftright.visibility = View.VISIBLE
                runTimer()
            }
        }
        binding.layBottom.btnReset.setOnClickListener {
            score = 0
            time = 0
            stopTimer()
        }

         */
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }
        binding.btnLeft.setOnClickListener {
            if (leftRightAdapter.datas[lastIdx].name == "LEFT")
                popItem()
            else
                toastWrong()
        }
        binding.btnRight.setOnClickListener {
            if (leftRightAdapter.datas[lastIdx].name == "RIGHT")
                popItem()
            else
                toastWrong()
        }

        mToast = createToast()
        initRecycler()
        runTimer()
    }

    private fun popItem() {
        score++
        binding.tvScoreLeftright.text = score.toString()
        leftRightAdapter.datas.removeAt(lastIdx)
        leftRightAdapter.datas.apply {
            val nextItem = (0.. 1).random()
            if (nextItem % 2 == 0)
                add(startIdx, LeftRightData(name = defaultItems[0].toString(), imageID = R.drawable.mushroom_b))
            else
                add(startIdx, LeftRightData(name = defaultItems[1].toString(), imageID = R.drawable.mushroom_z))
        }
        leftRightAdapter.notifyDataSetChanged()
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
            //binding.rvLeftright.visibility = View.GONE
            binding.rvLeftright.visibility = View.INVISIBLE
            binding.btnLeft.isEnabled = false
            binding.btnRight.isEnabled = false
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            //binding.cntButton.isVisible = true
            binding.rvLeftright.visibility = View.VISIBLE
            binding.btnLeft.isEnabled = true
            binding.btnRight.isEnabled = true
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
                    binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, score.toString())
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@LeftRightActivity)
                    mDialog.myDig("Score", score)

                    timerTask?.cancel()
                    //init()
                }
            }
        }
    }
    private fun setDatas() {
        val tmpDatas = mutableListOf<LeftRightData>()
        tmpDatas.apply {
            val range = (0..1)
            for (i in 0 .. lastIdx)
                if (range.random() % 2 == 0)
                    add(LeftRightData(name = defaultItems[0].toString(), imageID = R.drawable.mushroom_b))
                else
                    add(LeftRightData(name = defaultItems[1].toString(), imageID = R.drawable.mushroom_z))

            leftRightAdapter.datas = tmpDatas
            leftRightAdapter.notifyDataSetChanged()
        }
    }
    private fun initRecycler() {
        init()
        setDatas()
        binding.rvLeftright.adapter = leftRightAdapter
        binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, "0")
    }
    fun init() {
        //time = 0
        score = 0
        isOver = false
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvScoreLeftright.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.btnLeft.isEnabled = true
        binding.btnRight.isEnabled = true
        binding.tvBestScore.text = "최고기록: " + prefs.getSharedPrefs(gameName, "0")
        //setRadioState(true, binding.layBottom.radioGroup)
        binding.layTime.pgBar.max = time
        binding.rvLeftright.visibility = View.VISIBLE
        timerTask?.cancel()
    }
}