package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityInitialQuizBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.min

class InitialQuizActivity : AppCompatActivity() {
    private var mBinding: ActivityInitialQuizBinding? = null
    private val binding get() = mBinding!!
    lateinit var mAlertDialog: AlertDialog
    lateinit var mDialogView: View
    val defaultInitials = listOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
    val defaultInitialsCnt = defaultInitials.size

    var timerTask: Timer?= null
    var time = 0
    var isOver = false

    var resQ = false
    var resD = false
    var wordLength = -1

    //val gameName = "InitialQuiz"
    val api = NaverAPI.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_quiz)

        mBinding = ActivityInitialQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDialogView = LayoutInflater.from(this).inflate(R.layout.result_custom_dialog, null)
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
            .setTitle("Result")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()

        binding.btnHome.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnStart.setOnClickListener {
            allocQuest()
            runTimer()
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
                    questCheck()
                    dictionaryCheck()
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
            questCheck()
            dictionaryCheck()
        }
        init()
    }

    private fun runTimer() {
        time = 1000
        binding.btnStart.isEnabled = false
        val secTextView = binding.tvTime
        val resTextView = mDialogView.findViewById<TextView>(R.id.tv_custom_result)
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
                    secTextView.text = "0초"
                    //mDialogView.findViewById<TextView>(R.id.tv_result).text = resQ.toString() + ", " + resD.toString()
                    if (!resQ)
                        resTextView.text = "Wrong Initial"
                    else if (!resD)
                        resTextView.text = "Wrong Word"
                    else
                        resTextView.text = "Correct"

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

    private fun init() {
        time = 0
        isOver = false
        binding.pgBar.max = 1000
        binding.tvItem.text = "_______"
        binding.tvTime.text = "0초"
        binding.etAnswer.text = null
        binding.etAnswer.clearFocus()
        binding.etAnswer.isEnabled = false
        binding.btnSubmit.isEnabled = false
        binding.btnStart.isEnabled = true
        binding.etAnswer.clearFocus()
        resD = false
        resQ = false
        wordLength = -1
        timerTask?.cancel()
    }

    private fun allocQuest() {
        var string = ""
        wordLength = 2
        //wordLength = (2 .. 4).random()
        for (i in 1..wordLength)
            string += defaultInitials[(0 until defaultInitialsCnt).random()]
        binding.tvItem.text = string

        binding.etAnswer.isEnabled = true
        binding.btnSubmit.isEnabled = true
    }

    private fun questCheck() {
        val ansString = binding.etAnswer.text
        val questString = binding.tvItem.text
        for (i in 0 until wordLength) {
            if ((Integer.parseInt(ansString[i]+"") < 0xAC00) || ((Integer.parseInt(ansString[i] + "") - 0xAC00) / 28 / 21) != defaultInitials.indexOf(questString[i]))
                return
        }
        resQ = true
    }

    private fun dictionaryCheck() {
        val keyword = binding.etAnswer.text.toString()
        api.getSearchNews(keyword).enqueue(object : Callback<ResultGetSearchDict> {
            override fun onResponse(
                call: Call<ResultGetSearchDict>,
                response: Response<ResultGetSearchDict>
            ) {
                val bodyTotal = response.body()?.total
                var foundflag = false
                var tmpTitle: String
                for (i in 0 until min(10, bodyTotal!!.toInt())) {
                    tmpTitle = response.body()!!.items[i].name
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                        tmpTitle = Html.fromHtml(tmpTitle, Html.FROM_HTML_MODE_LEGACY).toString()
                    else
                        tmpTitle = Html.fromHtml(tmpTitle).toString();
                    if (tmpTitle == keyword) {
                        foundflag = true
                        break
                    }
                }

                if (bodyTotal != 0 && foundflag)
                    resD = true
            }

            override fun onFailure(call: Call<ResultGetSearchDict>, t: Throwable) {
                Toast.makeText(this@InitialQuizActivity, "ERROR", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


