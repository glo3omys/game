package com.example.myapplication

import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import scavDatas

class MyDialog(context: Context) {
    var mContext = context
    var mBuilder = AlertDialog.Builder(mContext)
    lateinit var mDialogView: View
    lateinit var mAlertDialog: AlertDialog
    lateinit var nextIntent: Intent

    /*
        var mTitle: TextView
        mTitle.text = title
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24F)
        mBuilder.setCustomTitle(mTitle)
        */

    // score
    fun myDig(title: String, cnt: Int = -1) {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_tv_dialog, null)

        mDialogView.findViewById<TextView>(R.id.my_tv).text = cnt.toString()
        mDialogView.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
        mDialogView.findViewById<View>(R.id.lay_bottom).visibility = View.GONE

        mBuilder.setView(mDialogView)
            .setTitle(title)
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        val okButton = mDialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener {
            nextIntent = Intent(mContext, MainActivity::class.java)
            mContext.startActivity(nextIntent)

            mAlertDialog.dismiss()
        }
    }

    // edit item
    fun myDig(title: String, itemName: String, itemScore: Int) {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_et_dialog, null)

        mDialogView.findViewById<EditText>(R.id.et_name).setText(itemName)
        mDialogView.findViewById<EditText>(R.id.et_score).setText(itemScore.toString())

        mBuilder.setView(mDialogView)
            .setTitle(title)
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        val okButton = mDialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener {

            val name = mDialogView.findViewById<EditText>(R.id.et_name).text.toString()
            val score = mDialogView.findViewById<EditText>(R.id.et_score).text.toString().toInt()
            val data = ScavData(name, score, false)
            scavDatas.add(data)

            scavDatas.sortBy { it.score }
            mAlertDialog.dismiss()
        }
        val cancelButton = mDialogView.findViewById<Button>(R.id.btn_cancel)
        cancelButton.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }

    // game description
    fun myDig(title: String, gameName: String) {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_tv_dialog, null)
        val radioGroup = mDialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val btnPlay = mDialogView.findViewById<Button>(R.id.btn_ok)
        val btnBack = mDialogView.findViewById<Button>(R.id.btn_cancel)
        btnPlay.text = "Play"

        mBuilder.setView(mDialogView)
            .setTitle(title)
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        var time : Int = 0
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sec_10 -> time = 10
                R.id.sec_20 -> time = 20
                R.id.sec_30 -> time = 30
            }
            if (radioGroup.checkedRadioButtonId != -1)
                btnPlay.isEnabled = true
        }
        btnPlay.setOnClickListener() {
            if (gameName == "Taptap")
                nextIntent = Intent(mContext, TaptapActivity::class.java)
            else if (gameName == "Balloon")
                nextIntent = Intent(mContext, BalloonActivity::class.java)
            else if (gameName == "FindNumber")
                nextIntent = Intent(mContext, FindNumberActivity::class.java)
            else if (gameName == "LeftRight")
                nextIntent = Intent(mContext, LeftRightActivity::class.java)
            else if(gameName == "Math")
                nextIntent = Intent(mContext, MathActivity::class.java)
            else if (gameName == "MemoryCardGame")
                nextIntent = Intent(mContext, MemoryCardGameActivity::class.java)
            else if (gameName == "WhackAMole")
                nextIntent = Intent(mContext, WhackAMoleActivity::class.java)

            nextIntent.putExtra("time", time * 100)
            mContext.startActivity(nextIntent)
        }
        btnBack.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }
}