package com.example.myapplication

import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import scavDatas

class MyDialog(context: Context){
    var mContext = context
    var mBuilder = AlertDialog.Builder(mContext)
    lateinit var mDialogView: View
    lateinit var mAlertDialog: AlertDialog
    lateinit var nextIntent: Intent

    /*
        var m: TextView
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
            nextIntent = Intent(mContext, GameListActivity::class.java)
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

    fun myAlertDialog(errInfo: String) {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_tv_dialog, null)

        mDialogView.findViewById<TextView>(R.id.my_tv).text = "잘못된 ${errInfo}입니다"
        mDialogView.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
        mDialogView.findViewById<View>(R.id.lay_bottom).visibility = View.GONE

        mBuilder.setView(mDialogView)
            .setTitle(" ")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        val okButton = mDialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }

    fun createRoom(myID: String) {
        val database = FirebaseDatabase.getInstance()
        val roomRef = database.getReference("room")

        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_et_dialog, null)

        mDialogView.findViewById<EditText>(R.id.et_name).hint = "방제"
        mDialogView.findViewById<EditText>(R.id.et_score).visibility = View.GONE

        mBuilder.setView(mDialogView)
            .setTitle("create room")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        val okButton = mDialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener {
            val roomSeed = genSeed()
            val memberList = mutableListOf<String>()
            //memberList.add(myID)
            val newRoom = RoomData (
                title = mDialogView.findViewById<EditText>(R.id.et_name).text.toString(),
                master = myID,
                seed = roomSeed,
                memberCnt = 1,
                memberList = memberList
            )

            var newUser = UserData(name = myID, imageID = R.drawable.mushroom_z, readyState = false)
            val roomPk = roomRef.push().key.toString()
            roomRef.child(roomPk).setValue(newRoom)
            val myPk = roomRef.child(roomPk).child("memberList").push().key.toString()
            roomRef.child(roomPk).child("memberList").child(myPk).setValue(newUser)
            roomRef.child(roomPk).child("readyCnt").setValue(0)
            val seedRef = database.getReference("roomSeeds")
            seedRef.child(roomSeed).setValue(roomPk)

            nextIntent = Intent(mContext, LobbyActivity::class.java)
            nextIntent.putExtra("roomPk", roomPk)
            nextIntent.putExtra("myPk", myPk)
            nextIntent.putExtra("masterName", myID)

            //(mContext as SearchRoomActivity).finish()
            mContext.startActivity(nextIntent)
            mAlertDialog.dismiss()
        }
        val cancelButton = mDialogView.findViewById<Button>(R.id.btn_cancel)
        cancelButton.setOnClickListener() {
            mAlertDialog.dismiss()
        }

    }

    fun searchRoom(mActivity: SearchRoomActivity) {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_et_dialog, null)

        mDialogView.findViewById<EditText>(R.id.et_name).hint = "참여코드"
        mDialogView.findViewById<EditText>(R.id.et_score).visibility = View.GONE

        mBuilder.setView(mDialogView)
            .setTitle("find room")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        val okButton = mDialogView.findViewById<Button>(R.id.btn_ok)
        var mySeed : String
        okButton.setOnClickListener() {
            mySeed = mDialogView.findViewById<EditText>(R.id.et_name).text.toString()
            mActivity.searchRoomBySeed(mySeed)
            mAlertDialog.dismiss()
        }
        val cancelButton = mDialogView.findViewById<Button>(R.id.btn_cancel)
        cancelButton.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }

    fun exitRoom(roomPk: String) {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.my_tv_dialog, null)
        mDialogView.findViewById<View>(R.id.lay_bottom).visibility = View.GONE
        mDialogView.findViewById<TextView>(R.id.my_tv).text = "나갈거야?"

        val btnOk = mDialogView.findViewById<Button>(R.id.btn_ok)
        val btnBack = mDialogView.findViewById<Button>(R.id.btn_cancel)
        btnOk.text = "나가기"
        btnBack.text = "취소"

        mBuilder.setView(mDialogView)
            .setTitle(" ")
            .setCancelable(false)
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        btnOk.setOnClickListener() {
            // rm DB
            val prefs = PreferenceUtil(mContext)
            val database = FirebaseDatabase.getInstance()
            val myRoomRef = database.getReference("room").child(roomPk)
            val myID = prefs.getSharedPrefs("myID", "")
            var myPk = ""
            var memberCnt : Int
            var readyCnt : Int

            myRoomRef.get().addOnSuccessListener {
                memberCnt = it.child("memberCnt").value.toString().toInt()
                memberCnt--
                readyCnt = it.child("readyCnt").value.toString().toInt()

                if (memberCnt == 0) {
                    (mContext as LobbyActivity).removeListener()
                    val roomSeed = it.child("seed").value.toString()
                    myRoomRef.removeValue()
                    val mySeedRef = database.getReference("roomSeeds").child(roomSeed)
                    mySeedRef.get().addOnSuccessListener {
                        mySeedRef.removeValue()
                    }
                }
                else {
                    myRoomRef.child("memberCnt").setValue(memberCnt)
                    var myState = false
                    for (data in it.child("memberList").children)
                        if (data.child("name").value.toString() == myID) {
                            myPk = data.key.toString()
                            myState = data.child("readyState").value.toString().toBoolean()
                            break
                        }
                    if (it.child("master").value == myID) {
                        for (data in it.child("memberList").children) {
                            if (data.child("name").value.toString() != myID) {
                                myRoomRef.child("master").setValue(data.child("name").value.toString())
                                if (data.child("readyState").value.toString().toBoolean())
                                    readyCnt--
                                myRoomRef.child("memberList").child(data.key.toString()).child("readyState").setValue(false)
                                break
                            }
                        }
                    }
                    else if (myState)
                        readyCnt--
                    myRoomRef.child("memberList").child(myPk).removeValue()
                    myRoomRef.child("readyCnt").setValue(readyCnt)
                }

                mAlertDialog.dismiss()
                (mContext as LobbyActivity).finish()
            }
                .addOnFailureListener {
                    Log.e("ERROR", "FAIL TO MOD DB")
                    //startActivity(Intent(this, MainActivity::class.java))
                }
        }
        btnBack.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }

    private fun genSeed() : String {
        val charset = 'A' .. 'Z'
        var res = ""
        for (i in (1 .. 6))
            res += charset.random()
        return res
    }

}