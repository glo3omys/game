package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.InitializerViewModelFactoryBuilder
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityFindNumberBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
    var paused = false
    var nextNumber = 1
    val SPAN_COUNT = 4
    val gameName = "FindNumber"

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""
    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    var gameData = mutableListOf<FindNumberData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_number)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityFindNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findNumberAdapter = FindNumberAdapter(this)

        var gridLayoutManager = GridLayoutManager(applicationContext, SPAN_COUNT)
        binding.rvFindnum.layoutManager = gridLayoutManager

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
            this@FindNumberActivity.finish()
            startActivity(nextIntent)
        }
        binding.layMenu.btnPause.setOnClickListener {
            pauseTimer()
        }

        mToast = createToast()
        initRecycler()
        //runTimer()
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
        if (!paused) {
            paused = true
            binding.rvFindnum.visibility = View.GONE
            timerTask?.cancel()
        }
        else {
            paused = false
            binding.rvFindnum.visibility = View.VISIBLE
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
                    updateMyBestScore(this@FindNumberActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@FindNumberActivity)
                    if (roomPk.isEmpty()) {
                        mDialog.myDig("Score", score)
                    }
                    else {
                        myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)
                        mDialog.myDig("Rank", intent.getSerializableExtra("roomInfoData") as RoomInfoData)
                    }
                    timerTask?.cancel()
                    //init()
                    binding.rvFindnum.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initRecycler() {
        setDatas()
        init()
        binding.rvFindnum.adapter = findNumberAdapter
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
    }
    private fun setDatas() {
        val tmpData = mutableListOf<Int>()
        gameData.clear()
        tmpData.clear()
        if (masterName == myID) {
            tmpData.apply {
                for (i in (1 .. 20))
                    add(i)
                shuffle()
            }
            gameData.apply {
                for (data in tmpData)
                    add(FindNumberData(num = data, selected = false))
            }
            myRoomRef.child("gameInfo").child("gameData").setValue(tmpData)
        }
        else if (masterName != "") {
            myRoomRef.child("gameInfo").child("gameData").get().addOnSuccessListener {
                gameData.apply {
                    for (data in it.children)
                        add(FindNumberData(num = data.value.toString().toInt(), selected = false))
                }
            }
        }
        else {
            gameData.apply {
                for (i in (1 .. 20))
                    add(FindNumberData(num = i, selected = false))
                shuffle()
            }
        }
        findNumberAdapter.datas = gameData
        findNumberAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    fun init() {
        score = 0
        nextNumber = 1
        isOver = false
        binding.tvScoreFindnum.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        binding.layTime.pgBar.max = time
        //binding.rvFindnum.visibility = View.VISIBLE
        timerTask?.cancel()

        val mDialog = CountDownDialog(this@FindNumberActivity)
        mDialog.countDown()
        Handler().postDelayed({
            binding.rvFindnum.visibility = View.VISIBLE
            runTimer()
        }, 3100)
    }
}