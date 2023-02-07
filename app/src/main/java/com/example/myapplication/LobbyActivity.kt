package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    var masterFlag = false
    var roomPk = ""

    val database = Firebase.database
    lateinit var myRef : DatabaseReference
    private val userDatas = mutableListOf<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        masterFlag = intent.getBooleanExtra("master", false)
        roomPk = intent.getStringExtra("pk").toString()
        myRef = database.getReference("room").child(roomPk)

        lobbyAdapter = LobbyAdapter(this)

        init()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val dialog = MyDialog(this)
        dialog.goBack(roomPk)
    }

    private fun init() {
        binding.rvUsers.adapter = lobbyAdapter
        fetchData()

        // btn 권한 설정
    }

    fun fetchData() {
        myRef
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userDatas.clear()
                    for (data in snapshot.child("memberList").children) {
                        val name = data.getValue().toString()
                        userDatas.add(UserData(name = name, imageID = R.drawable.ic_launcher_foreground))
                    }
                    lobbyAdapter.datas = userDatas
                    lobbyAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}