package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.myapplication.databinding.ActivityInitialQuizBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.min

class InitialQuizActivity : AppCompatActivity() {
    private var mBinding: ActivityInitialQuizBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    /*lateinit var mAlertDialog: AlertDialog
    lateinit var mDialogView: View*/
    val defaultInitials = listOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
    val defaultInitialsCnt = defaultInitials.size

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

    var resQ = false
    var resD = false
    var qChecked = false
    var dChecked = false
    val wordLength = 2
    var questString = ""

    //val gameName = "InitialQuiz"
    val api = NaverAPI.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_quiz)
        prefs = PreferenceUtil(applicationContext)

        mBinding = ActivityInitialQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myID = prefs.getSharedPrefs("myID", "")
        val intent = intent
        time = intent.getIntExtra("time", 0) /* default value check */
        val roomInfoData = intent.getSerializableExtra("roomInfoData") as? RoomInfoData
        if (roomInfoData != null) {
            roomPk = roomInfoData.roomPk
            myPk = roomInfoData.myPk
            masterName = roomInfoData.masterName
        }
        myRoomRef = database.getReference("room").child(roomPk)

        if (masterName != "" && masterName != myID)
            questString = intent.getStringExtra("gameData").toString()
        else
            allocQuest()

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, GameListActivity::class.java)
            startActivity(nextIntent)
        }
        binding.etAnswer.addTextChangedListener(object: TextWatcher {
            val etAnswer = binding.etAnswer
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                /*
                if (s.toString().equals("\n"))
                    Toast.makeText(this@InitialQuizActivity, "Toast", Toast.LENGTH_SHORT).show()
                */
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val res = s.toString().replace(" ", "")
                if (!s.toString().equals(res)) {
                    etAnswer.setText(res)
                    etAnswer.setSelection(res.length)
                }
                binding.btnSubmit.isEnabled = !s.toString().equals("")

                if (!s.toString().equals("") && s.toString()[s!!.length - 1].toString() == "\n") {
                    val res = s.toString().replace("\n", "")
                    if (!s.toString().equals(res)) {
                        etAnswer.setText(res)
                        etAnswer.setSelection(res.length)
                    }
                    if (res.equals(""))
                        return
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etAnswer.windowToken, 0)
                    etAnswer.isEnabled = false
                    binding.btnSubmit.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                /*val res = s.toString().replace(" ", "")
                if (!s.toString().equals(res)) {
                    etAnswer.setText(res)
                    etAnswer.setSelection(res.length)
                }

                 */
            }
        })
        binding.btnSubmit.setOnClickListener {
            val etAnswer = binding.etAnswer
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etAnswer.windowToken, 0)
            etAnswer.isEnabled = false
            binding.btnSubmit.isEnabled = false
        }
        init()
    }

    private fun runTimer() {
        //time = 1000
        //binding.btnStart.isEnabled = false
        val secTextView = binding.layTime.tvTime
        //val resTextView = mDialogView.findViewById<TextView>(R.id.my_tv)
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
                    secTextView.text = "0초"

                    while (!qChecked)
                        questCheck()
                    while (!dChecked) {
                        dictionaryCheck()
                        //Thread.sleep(1000)
                    }

                    myRoomRef.child("gameInfo").child("gameScore").child(myID).setValue(resD && resQ)

                    val mDialog = MyDialog(this@InitialQuizActivity)
                    if (roomPk.isEmpty())
                        mDialog.myDig("Score")
                    else
                        mDialog.myDig("Rank", intent.getSerializableExtra("roomInfoData") as RoomInfoData, true)

                    timerTask?.cancel()
                }
            }
        }
    }

    private fun init() {
        //allocQuest()

        //time = 0
        isOver = false
        //binding.layTime.pgBar.max = 1000
        binding.layTime.pgBar.max = time
        binding.tvItem.text = questString
        binding.layTime.tvTime.text = "0초"
        binding.etAnswer.text = null
        binding.etAnswer.clearFocus()
        resD = false
        resQ = false
        //wordLength = -1
        timerTask?.cancel()

        runTimer()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun allocQuest() {
        questString = ""
        //wordLength = 2

        //wordLength = (2 .. 4).random()

        for (i in 1..wordLength)
            questString += defaultInitials[(0 until defaultInitialsCnt).random()]

        if (masterName == myID)
            myRoomRef.child("gameInfo").child("gameData").setValue(questString)
    }

    private fun questCheck() {
        val ansString = binding.etAnswer.text
        val questString = binding.tvItem.text
        if (ansString.length != questString.length) {
            qChecked = true
            return
        }
        for (i in 0 until wordLength) {
            if ((ansString[i].toString().toIntOrNull()?.let { it < 0xAC00 } ?: false) || (ansString[i].toInt() - 0xAC00) / 28 / 21 != defaultInitials.indexOf(questString[i]) ) {
                qChecked = true
                return
            }
        }
        resQ = true
        qChecked = true
    }

    private fun dictionaryCheck() {
        val keyword = binding.etAnswer.text.toString()
        api.getSearchNews(keyword).enqueue(object : Callback<ResultGetSearchDict> {
            override fun onResponse(
                call: Call<ResultGetSearchDict>,
                response: Response<ResultGetSearchDict>
            ) {
                val bodyTotal = response.body()?.total
                var foundFlag = false
                var tmpTitle: String
                for (i in 0 until min(10, bodyTotal!!.toInt())) {
                    tmpTitle = response.body()!!.items[i].name
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                        tmpTitle = Html.fromHtml(tmpTitle, Html.FROM_HTML_MODE_LEGACY).toString()
                    else
                        tmpTitle = Html.fromHtml(tmpTitle).toString();
                    if (tmpTitle == keyword) {
                        foundFlag = true
                        break
                    }
                }
                resD = bodyTotal != 0 && foundFlag
                dChecked = true
            }

            override fun onFailure(call: Call<ResultGetSearchDict>, t: Throwable) {
                Toast.makeText(this@InitialQuizActivity, "ERROR", Toast.LENGTH_SHORT).show()
                dChecked = true
            }
        })
    }
}


