package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.databinding.ActivitySearchRoomBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchRoomActivity : AppCompatActivity() {
    private var mBinding: ActivitySearchRoomBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    lateinit var roomAdapter: SearchRoomAdapter

    val database = Firebase.database
    lateinit var myRef : DatabaseReference
    val roomDatas = mutableListOf<RoomData>()
    val seedToPk = hashMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_room)
        prefs = PreferenceUtil(applicationContext)

        myRef = database.getReference()

        mBinding = ActivitySearchRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomAdapter = SearchRoomAdapter(this)

        binding.btnCreateRoom.setOnClickListener() {
            val dialog = MyDialog(this)
            dialog.createRoom(prefs.getSharedPrefs("myID", "GUEST"))
        }

        binding.btnSearchRoom.setOnClickListener() {
            val dialog = MyDialog(this)
            dialog.searchRoom(this)
        }

        initRecycler()
    }

    private fun initRecycler() {
        binding.rvRoomlist.apply { addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL)) }
        binding.rvRoomlist.adapter = roomAdapter

        fetchData()
    }

    fun searchRoomBySeed(seed : String) {
        if (seedToPk.containsKey(seed)) {
            val roomPk = seedToPk[seed].toString()

            val myRoomRef = database.getReference("room").child(roomPk)
            myRoomRef.get().addOnSuccessListener {
                var memberCnt = it.child("memberCnt").value.toString().toInt()
                memberCnt++
                myRoomRef.child("memberCnt").setValue(memberCnt)

                val newUser =  UserData(name = prefs.getSharedPrefs("myID", "GUEST"), imageID = 0, readyState = false)
                val myPk = myRoomRef.child("memberList").push().key.toString()
                val masterName = it.child("master").value.toString()
                myRoomRef.child("memberList").child(myPk).setValue(newUser)

                val nextIntent = Intent(this, LobbyActivity::class.java)
                nextIntent.putExtra("roomPk", seedToPk[seed])
                nextIntent.putExtra("myPk", myPk)
                nextIntent.putExtra("masterName", masterName)
                startActivity(nextIntent)
            }
                .addOnFailureListener {
                    Log.e("ERROR", "FAIL TO MOD DB")
                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
        else {
            val dialog = MyDialog(this)
            dialog.myAlertDialog("참여코드")
        }
    }

    fun fetchData() {
        myRef
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    roomDatas.clear()
                    for (data in snapshot.child("room").children) {
                        val master = data.child("master").value.toString()
                        val title = data.child("title").value.toString()
                        val seed = data.child("seed").value.toString()
                        val memberCnt = data.child("memberCnt").value.toString().toInt()
                        //val memberList = data.child("memberList").value as MutableList<String>

                        roomDatas.add(RoomData(master = master, title = title, seed = seed, memberCnt = memberCnt))
                    }
                    for (data in snapshot.child("roomSeeds").children) {
                        val seed = data.key.toString()
                        val pk = data.value.toString()

                        seedToPk[seed] = pk
                    }

                    roomAdapter.datas = roomDatas
                    roomAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}