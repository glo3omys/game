package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityLeftRightBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import updateMyBestScore
import java.util.*
import kotlin.concurrent.timer

class LeftRightActivity : AppCompatActivity() {
    lateinit var leftRightAdapter: LeftRightAdapter
    val defaultItems = listOf("LEFT", "RIGHT")

    private var mBinding: ActivityLeftRightBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    var timerTask: Timer?= null
    var time = 0
    var isOver = false
    var score = 0
    var paused = false
    val startIdx = 0
    var lastIdx = 0 // size = 7(0 .. 6)
    val gameName = "LeftRight"

    var myID = ""
    var masterName = ""
    var roomPk = ""
    var myPk = ""
    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    var gameData = mutableListOf<LeftRightData>()

    lateinit var mToast: Toast
    lateinit var customToastLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_left_right)
        prefs = PreferenceUtil(applicationContext)

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

        binding.rvLeftright.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                if (position != lastIdx)
                    outRect.bottom -= 90
            }
        })

        binding.layMenu.btnLayQuit.setOnClickListener {
            timerTask?.cancel()
            val nextIntent = Intent(this, GameListActivity::class.java)
            this@LeftRightActivity.finish()
            startActivity(nextIntent)
        }
        binding.layMenu.btnPause.setOnClickListener {
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

        binding.btnLeft.visibility = View.INVISIBLE
        binding.btnRight.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnLeft.visibility = View.VISIBLE
            binding.btnRight.visibility = View.VISIBLE
        }, 1000)
    }

    private fun pauseTimer() {
        if (!paused) {
            paused = true
            binding.rvLeftright.visibility = View.INVISIBLE
            binding.btnLeft.isEnabled = false
            binding.btnRight.isEnabled = false
            timerTask?.cancel()
        }
        else {
            paused = false
            binding.rvLeftright.visibility = View.VISIBLE
            binding.btnLeft.isEnabled = true
            binding.btnRight.isEnabled = true
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
                    updateMyBestScore(this@LeftRightActivity, gameName, score.toString())
                    binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, score.toString())}"
                    secTextView.text = "0초"

                    val mDialog = MyDialog(this@LeftRightActivity)
                    if (roomPk.isEmpty()) {
                        mDialog.myDig("Score", score)
                    }
                    else {
                        myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(score)
                        mDialog.myDig("Rank", intent.getSerializableExtra("roomInfoData") as RoomInfoData)
                    }
                    timerTask?.cancel()
                }
            }
        }
    }
    private fun setDatas() {
        if (masterName == myID)
            myRoomRef.child("gameInfo").child("gameData").setValue("0")

        val tmpDatas = mutableListOf<LeftRightData>()
        val range = (0..1)
        for (i in 0 .. lastIdx)
            if (range.random() % 2 == 0)
                tmpDatas.add(LeftRightData(name = defaultItems[0].toString(), imageID = R.drawable.mushroom_b))
            else
                tmpDatas.add(LeftRightData(name = defaultItems[1].toString(), imageID = R.drawable.mushroom_z))

        leftRightAdapter.datas = tmpDatas
        leftRightAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun initRecycler() {
        setDatas()
        init()
        binding.rvLeftright.adapter = leftRightAdapter
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
    }
    fun init() {
        score = 0
        isOver = false
        binding.tvScoreLeftright.text = "0"
        binding.layTime.tvTime.text = "0초"
        binding.btnLeft.isEnabled = true
        binding.btnRight.isEnabled = true
        binding.tvBestScore.text = "최고기록: ${prefs.getSharedPrefs(gameName, "0")}"
        binding.layTime.pgBar.max = time
        //binding.rvLeftright.visibility = View.VISIBLE
        timerTask?.cancel()

        val mDialog = CountDownDialog(this@LeftRightActivity)
        mDialog.countDown()
        Handler().postDelayed({
            binding.rvLeftright.visibility = View.VISIBLE
            runTimer()
        }, 3100)
    }
}