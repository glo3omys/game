package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.myapplication.databinding.ActivityLobbyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LobbyActivity : AppCompatActivity() {
    private var mBinding: ActivityLobbyBinding? = null
    private val binding get() = mBinding!!
    lateinit var lobbyAdapter: LobbyAdapter
    lateinit var prefs : PreferenceUtil

    var roomPk = ""
    var myPk = ""
    var masterName = ""
    var myID = ""

    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    private val userDatas = mutableListOf<UserData>()
    var startFlag = false
    var gameData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceUtil(applicationContext)

        lobbyAdapter = LobbyAdapter(this)
        binding.rvUsers.adapter = lobbyAdapter

        val roomInfoData = intent.getSerializableExtra("roomInfoData") as? RoomInfoData
        if (roomInfoData != null) {
            roomPk = roomInfoData.roomPk
            myPk = roomInfoData.myPk
            masterName = roomInfoData.masterName
        }

        myRoomRef = database.getReference("room").child(roomPk)
        myID = prefs.getSharedPrefs("myID", "")
        dbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!startFlag && snapshot.child("gameInfo").child("gameData").value != null) {
                    if (snapshot.child("gameInfo").child("gameData").value is String)
                        gameData = snapshot.child("gameInfo").child("gameData").value.toString()
                    startFlag = true
                    startGame()
                }

                userDatas.clear()
                masterName = snapshot.child("master").value.toString()
                val memberCnt = snapshot.child("memberCnt").value.toString().toInt()
                val readyCnt = snapshot.child("readyCnt").value.toString().toInt()

                for (data in snapshot.child("memberList").children) {
                    val name = data.child("name").value.toString()
                    var readyState = data.child("readyState").value as Boolean
                    if (name == masterName)
                        readyState = false
                    userDatas.add(UserData(name = name, imageID = R.drawable.ic_launcher_foreground, readyState = readyState))
                }
                if (masterName == myID)
                    binding.btnReady.isEnabled = (memberCnt - 1 == readyCnt) && (memberCnt != 1)
                setVisibility()
                binding.tvSelectedGame.text = snapshot.child("gameInfo").child("gameTitle").value.toString()

                lobbyAdapter.datas = userDatas
                lobbyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        myRoomRef.addValueEventListener(dbListener!!)

        binding.btnReady.setOnClickListener() {
            var readyFlag = false
            val btnReady = binding.btnReady
            if (!startFlag && btnReady.text == "시작") {
                startFlag = true
                startGame()
            } else {
                myRoomRef.get().addOnSuccessListener {
                    var readyCnt: Int = it.child("readyCnt").value.toString().toInt()
                    readyFlag = it.child("memberList").child(myPk).child("readyState").value.toString().toBoolean()
                    if (readyFlag) {
                        readyCnt--
                        btnReady.text = "준비"
                    } else {
                        readyCnt++
                        btnReady.text = "준비완료"
                    }
                    readyFlag = !readyFlag
                    myRoomRef.child("memberList").child(myPk).child("readyState").setValue(readyFlag)
                    myRoomRef.child("readyCnt").setValue(readyCnt)
                }
            }
        }
        initSpinner()
        setVisibility()
    }

    private fun setVisibility() {
        if (masterName == myID) {
            binding.btnReady.text = "시작"
            //binding.btnReady.isEnabled = false
            binding.spinnerGameList.visibility = View.VISIBLE
            binding.tvSelectedGame.visibility = View.GONE
        }
        else {
            binding.spinnerGameList.visibility = View.GONE
            binding.tvSelectedGame.visibility = View.VISIBLE
        }
    }

    private fun initSpinner() {
        val spinner = binding.spinnerGameList
        val gameList = resources.getStringArray(R.array.games).toMutableList()
        val spinnerAdapter = ArrayAdapter<String>(this, R.layout.recycler_spinner, gameList)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //binding.tvSelectedGame.text = gameList[position]
                myRoomRef.child("gameInfo").child("gameTitle").setValue(gameList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        myRoomRef.child("gameInfo").child("gameTitle").get().addOnSuccessListener {
            if (it.value.toString() != null)
                spinner.setSelection(spinnerAdapter.getPosition(it.value.toString()))
            else
                spinner.setSelection(0)
        }
    }

    override fun onDestroy() {
        removeListener()
        super.onDestroy()
    }

    private fun startGame() {
        var gameTitle : String
        if (masterName == myID)
            gameTitle = binding.spinnerGameList.selectedItem.toString()
        else
            gameTitle = binding.tvSelectedGame.text.toString()

        var nextIntent = Intent(this@LobbyActivity, MainActivity::class.java)
        if (gameTitle == "Balloon")
            nextIntent = Intent(this@LobbyActivity, BalloonActivity::class.java)
        else if (gameTitle == "FindNumber")
            nextIntent = Intent(this@LobbyActivity, FindNumberActivity::class.java)
        else if (gameTitle == "InitialQuiz")
            nextIntent = Intent(this@LobbyActivity, InitialQuizActivity::class.java)
        else if (gameTitle == "LeftRight")
            nextIntent = Intent(this@LobbyActivity, LeftRightActivity::class.java)
        else if (gameTitle == "Math")
            nextIntent = Intent(this@LobbyActivity, MathActivity::class.java)
        else if (gameTitle == "MemoryCardGame")
            nextIntent = Intent(this@LobbyActivity, MemoryCardGameActivity::class.java)
        else if (gameTitle == "Taptap")
            nextIntent = Intent(this@LobbyActivity, TaptapActivity::class.java)
        else if (gameTitle == "WhackAMole")
            nextIntent = Intent(this@LobbyActivity, WhackAMoleActivity::class.java)

        val roomInfoData = RoomInfoData(roomPk = roomPk, myPk = myPk, masterName = masterName)

        this@LobbyActivity.finish()
        nextIntent.putExtra("roomInfoData", roomInfoData)
        nextIntent.putExtra("time", 10 * 100)
        if (gameData != "")
            nextIntent.putExtra("gameData", gameData)
        startActivity(nextIntent)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val dialog = MyDialog(this)
        dialog.exitRoom(roomPk)
    }
    fun removeListener() {
        if (dbListener != null) {
            myRoomRef.removeEventListener(dbListener!!)
            dbListener = null
        }
    }
}
