package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var mAlertDialog: AlertDialog

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0
    val gameName = "LeftRight"

    lateinit var mToast: Toast
    lateinit var customToastLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_left_right)

        mBinding = ActivityLeftRightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        leftRightAdapter = LeftRightAdapter(this)

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
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
            if (binding.radioGroup.checkedRadioButtonId != -1)
                binding.btnStart.isEnabled = true
        }
        binding.btnStart.setOnClickListener {
            if (binding.radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this@LeftRightActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                init()
                setDatas()
                setRadioState(false, binding.radioGroup)
                time *= 100
                binding.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.btnStart.isEnabled = false
                binding.btnLeft.isEnabled = true
                binding.btnRight.isEnabled = true
                binding.rvLeftright.visibility = View.VISIBLE
                runTimer(mDialogView)
            }
        }
        binding.btnReset.setOnClickListener {
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
        binding.btnLeft.setOnClickListener {
            if (leftRightAdapter.datas[4].name == "LEFT")
                popItem()
            else
                toastWrong()
        }
        binding.btnRight.setOnClickListener {
            if (leftRightAdapter.datas[4].name == "RIGHT")
                popItem()
            else
                toastWrong()
        }

        mToast = createToast()
        initRecycler()
    }

    private fun popItem() {
        score++
        binding.tvScoreLeftright.text = score.toString()
        leftRightAdapter.datas.removeAt(4)
        leftRightAdapter.datas.apply {
            val nextItem = (0.. 1).random()
            if (nextItem % 2 == 0)
                add(0, LeftRightData(name = defaultItems[0].toString(), imageID = R.drawable.mushroom_b))
            else
                add(0, LeftRightData(name = defaultItems[1].toString(), imageID = R.drawable.mushroom_z))
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

    private fun pauseTimer(mDialogView: View) {
        var pauseBtn = binding.btnPause
        if (pauseBtn.text == "PAUSE") {
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
            runTimer(mDialogView)
        }
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer(mDialogView: View) {
        val secTextView = binding.tvTime
        val progressBar = binding.pgBar

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
                    binding.tvBestScore.text = prefs.getSharedPrefs(gameName, score.toString())
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
    private fun setDatas() {
        val tmpDatas = mutableListOf<LeftRightData>()
        tmpDatas.apply {
            val range = (0..1)
            for (i in 0 until 5)
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
        binding.tvBestScore.text = prefs.getSharedPrefs(gameName, "0")
    }
    fun init() {
        time = 0
        score = 0
        isOver = false
        binding.radioGroup.clearCheck()
        binding.tvScoreLeftright.text = "0"
        binding.tvTime.text = "0초"
        binding.btnPause.text = "PAUSE"
        binding.btnPause.isEnabled = false
        //binding.rvLeftright.visibility = View.GONE
        binding.rvLeftright.visibility = View.INVISIBLE
        binding.btnStart.isEnabled = false
        binding.btnLeft.isEnabled = false
        binding.btnRight.isEnabled = false
        binding.tvBestScore.text = prefs.getSharedPrefs(gameName, "0")
        setRadioState(true, binding.radioGroup)
        timerTask?.cancel()
    }
}