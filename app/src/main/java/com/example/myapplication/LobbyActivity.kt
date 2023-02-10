package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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

    val database = Firebase.database
    lateinit var myRoomRef : DatabaseReference
    var dbListener: ValueEventListener? = null
    private val userDatas = mutableListOf<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceUtil(applicationContext)

        lobbyAdapter = LobbyAdapter(this)
        binding.rvUsers.adapter = lobbyAdapter

        roomPk = intent.getStringExtra("roomPk").toString()
        myPk = intent.getStringExtra("myPk").toString()
        masterName = intent.getStringExtra("masterName").toString()
        myRoomRef = database.getReference("room").child(roomPk)
        dbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
                if (masterName == prefs.getSharedPrefs("myID", ""))
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
            if (btnReady.text == "시작") {
                /*myRoomRef.get().addOnSuccessListener {
                    if (it.child("readyCnt").value.toString()
                            .toInt() == it.child("memberCnt").value.toString().toInt() - 1
                    )
                    //startGame()
                        Toast.makeText(this@LobbyActivity, "SUCCESS", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(this@LobbyActivity, "FAIL", Toast.LENGTH_SHORT).show()
                    }
                }*/
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
        /*if (masterName == prefs.getSharedPrefs("myID", ""))
            initSpinner()
        else
            binding.spinnerGameList.visibility = View.GONE

         */
        initSpinner()
        setVisibility()
    }

    private fun setVisibility() {
        if (masterName == prefs.getSharedPrefs("myID", "")) {
            binding.btnReady.text = "시작"
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
    }

    override fun onDestroy() {
        removeListener()
        super.onDestroy()
    }

    private fun startGame() {

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
