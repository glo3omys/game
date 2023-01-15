package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.MainActivity.Companion.prefs
import com.example.myapplication.databinding.ActivityMathBinding
import setRadioState
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class MathActivity: AppCompatActivity() {
    lateinit var mathAdapter: MathAdapter
    val operations = listOf<String>("+", "-", "*")

    private var mBinding: ActivityMathBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var score = 0
    var numCnt = 0
    val SPAN_COUNT = 5

    val gameName = "Math"

    lateinit var mToast: Toast
    lateinit var customToastLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)

        mBinding = ActivityMathBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mathAdapter = MathAdapter(this)

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvMath.layoutManager = gridLayoutManager

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
                Toast.makeText(this@MathActivity, "CHECK ERROR", Toast.LENGTH_SHORT).show()
            else {
                setDatas()
                allocQuest()
                setRadioState(false, binding.layBottom.radioGroup)

                time *= 100
                binding.layTime.pgBar.max = time
                binding.btnPause.isEnabled = true
                binding.layBottom.btnStart.isEnabled = false
                binding.rvMath.visibility = View.VISIBLE
                runTimer(mDialogView, mBuilder)
            }
        }
        binding.layBottom.btnReset.setOnClickListener() {
            stopTimer()
        }
        binding.btnPause.setOnClickListener {
            pauseTimer(mDialogView, mBuilder)
        }
        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }

        mToast = createToast()
        initRecycler()
    }

    private fun pauseTimer(mDialogView: View, mBuilder: AlertDialog.Builder) {
        var pauseBtn = binding.btnPause
        if (pauseBtn.text == "PAUSE") {
            binding.rvMath.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            binding.rvMath.visibility = View.VISIBLE
            pauseBtn.text = "PAUSE"
            runTimer(mDialogView, mBuilder)
        }
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer(mDialogView: View, mBuilder: AlertDialog.Builder) {
        binding.rvMath.visibility = View.VISIBLE
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

    private fun initRecycler() {
        init()
        setDatas()
        binding.tvBestScore.text = prefs.getSharedPrefs(gameName, "0")
        binding.rvMath.adapter = mathAdapter
    }

    private fun setDatas() {
        val tmpDatas = mutableListOf<MathData>()
        tmpDatas.apply {
            for (i in -7 .. 7)
                add(MathData(num = i, selected = false))
            tmpDatas.shuffle()
            mathAdapter.datas = tmpDatas
            mathAdapter.notifyDataSetChanged()
        }
    }

    private fun allocQuest() {
        numCnt = 0
        binding.tvOp1.text = operations[(0..2).random()]
        binding.tvOp2.text = operations[(0..2).random()]

        binding.tvNum1.text = "  "
        binding.tvNum2.text = "  "
        binding.tvNum3.text = "  "

        binding.tvNum1.setBackgroundResource(R.drawable.textview_edge) /* CHECK */
    }
    fun popNumber(number: Int) {
        val nowTv : TextView
        val nextTv : TextView

        if (numCnt == 0) {
            nowTv = binding.tvNum1
            nextTv = binding.tvNum2
            nextTv.setBackgroundResource(R.drawable.textview_edge)
        }
        else if (numCnt == 1) {
            nowTv = binding.tvNum2
            nextTv = binding.tvNum3
            nextTv.setBackgroundResource(R.drawable.textview_edge)
        }
        else
            nowTv = binding.tvNum3

        numCnt++
        nowTv.text = number.toString()
        nowTv.setBackgroundResource(0)

        if (numCnt == 3)
            calculate()
    }

    private fun calculate() {
        val num1 = binding.tvNum1.text.toString().toInt()
        val num2 = binding.tvNum2.text.toString().toInt()
        val num3 = binding.tvNum3.text.toString().toInt()
        val op1 = binding.tvOp1.text
        val op2 = binding.tvOp2.text
        var res = 0
        var string : String = ""

        if (op2 == "*") {
            res = num2 * num3
            when (op1) {
                "*" -> res *= num1
                "+" -> res += num1
                "-" -> res = num1 - res
            }
        }
        else {
            when (op1) {
                "*" -> res = num1 * num2
                "+" -> res = num1 + num2
                "-" -> res = num1 - num2
            }
            when (op2) {
                "+" -> res += num3
                "-" -> res -= num3
            }
        }

        if (binding.tvHistory.text.toString() != "")
            string = " + "
        string += "( ${num1} ${op1} ${num2} ${op2} ${num3} )\n"

        //if (binding.tvHistory.text.toString() != "")
        //    mToast.cancel()
        if (mToast != null) {
            mToast.cancel()
            mToast = createToast()
        }
        val tstScore = customToastLayout.findViewById<TextView>(R.id.tv_score_toast)
        tstScore.text = res.toString()
        mToast.show()

        score += res
        binding.tvScoreMath.text = "SCORE: " + score.toString()
        binding.tvHistory.append(string)
        allocQuest()
    }

    private fun createToast(): Toast {
        customToastLayout = layoutInflater.inflate(R.layout.score_custom_toast,null)
        return Toast(this).apply {
            setGravity(Gravity.CENTER, 0, 0)
            duration = Toast.LENGTH_SHORT
            view = customToastLayout
        }
    }

    fun init() {
        time = 0
        score = 0
        isOver = false
        numCnt = 0
        binding.layBottom.radioGroup.clearCheck()
        binding.tvHistory.text = ""
        binding.tvScoreMath.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.btnPause.text = "PAUSE"
        binding.tvNum1.text = "  "
        binding.tvNum2.text = "  "
        binding.tvNum3.text = "  "
        binding.btnPause.isEnabled = false
        binding.rvMath.visibility = View.GONE
        binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = prefs.getSharedPrefs(gameName, "0")
        setRadioState(true, binding.layBottom.radioGroup)
        timerTask?.cancel()
    }
}