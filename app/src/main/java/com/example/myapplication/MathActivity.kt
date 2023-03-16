package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityMathBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class MathActivity: AppCompatActivity() {
    lateinit var mathAdapter: MathAdapter
    val operations = listOf<String>("+", "-", "*")

    private var mBinding: ActivityMathBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""
    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    var gameData = mutableListOf<MathData>()

    var score = 0
    var numCnt = 0
    val SPAN_COUNT = 5

    val gameName = "Math"

    lateinit var mToast: Toast
    lateinit var customToastLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityMathBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mathAdapter = MathAdapter(this)

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvMath.layoutManager = gridLayoutManager

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

        mToast = createToast()
        initRecycler()
        runTimer()
    }

    private fun pauseTimer() {
        var pauseBtn = binding.btnPause
        /*if (pauseBtn.text == "PAUSE") {
            binding.rvMath.visibility = View.GONE
            pauseBtn.text = "PLAY"
            timerTask?.cancel()
        }
        else {
            binding.rvMath.visibility = View.VISIBLE
            pauseBtn.text = "PAUSE"
            runTimer()
        }*/
    }
    private fun stopTimer() {
        timerTask?.cancel()
        init()
    }
    fun runTimer() {
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
                    updateMyBestScore(this@MathActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)

                    val mDialog = MyDialog(this@MathActivity)
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

    private fun initRecycler() {
        init()
        setDatas()
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        binding.rvMath.adapter = mathAdapter
    }

    private fun setDatas() {
        if (masterName != "" && masterName != myID) {
            myRoomRef.child("gameInfo").child("gameData").get().addOnSuccessListener {
                    for (data in it.children)
                        gameData.add(MathData(num = data.value.toString().toInt(), selected = false))
            }
        }
        else {
            val tmpData = mutableListOf<Int>()
            for (i in -7 .. 7)
                tmpData.add(i)
            tmpData.shuffle()
            for (data in tmpData)
                gameData.add(MathData(num = data, selected = false))

            if (masterName == myID) {
                myRoomRef.child("gameInfo").child("gameData").setValue(tmpData)
            }
        }
        mathAdapter.datas = gameData
        mathAdapter.notifyDataSetChanged()
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

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    fun init() {
        //time = 0
        score = 0
        isOver = false
        numCnt = 0
        //binding.layBottom.radioGroup.clearCheck()
        binding.tvHistory.text = ""
        binding.tvScoreMath.text = "0"
        binding.layTime.tvTime.text = "0초"
        //binding.btnPause.text = "PAUSE"
        binding.tvNum1.text = "  "
        binding.tvNum2.text = "  "
        binding.tvNum3.text = "  "
        binding.btnPause.isEnabled = false
        //binding.layBottom.btnStart.isEnabled = false
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        //setRadioState(true, binding.layBottom.radioGroup)
        binding.layTime.pgBar.max = time
        binding.rvMath.visibility = View.VISIBLE
        timerTask?.cancel()
    }
}