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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchRoomActivity : AppCompatActivity() {
    private var mBinding: ActivitySearchRoomBinding? = null
    private val binding get() = mBinding!!
    lateinit var roomAdapter: SearchRoomAdapter

    val database = Firebase.database
    val myRef = database.getReference()
    val roomDatas = mutableListOf<RoomData>()
    val seedToPk = hashMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_room)

        mBinding = ActivitySearchRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomAdapter = SearchRoomAdapter(this)

        binding.btnCreateRoom.setOnClickListener() {
            val dialog = MyDialog(this)
            dialog.createRoom("master")
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
            val pk = seedToPk[seed].toString()
            val memberList = mutableListOf<String>()
            var memberCnt = -1

            myRef.child("room").child(pk).get().addOnSuccessListener {
                for (data in it.child("memberList").children)
                    memberList.add(data.value.toString())
                memberList.add("돼라")

                memberCnt = it.child("memberCnt").value.toString().toInt()
                memberCnt++

                myRef.child("room").child(pk).child("memberCnt").setValue(memberCnt)
                myRef.child("room").child(pk).child("memberList").setValue(memberList)

                val nextIntent = Intent(this, LobbyActivity::class.java)
                nextIntent.putExtra("master", false)
                nextIntent.putExtra("pk", seedToPk[seed])
                startActivity(nextIntent)
            }
                .addOnFailureListener {
                    Log.e("ERROR", "FAIL TO MOD DB")
                    startActivity(Intent(this, MainActivity::class.java))
                }

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
                        val memberList = data.child("memberList").value as MutableList<String>

                        roomDatas.add(RoomData(master = master, title = title, seed = seed, memberCnt = memberCnt, memberList = memberList))
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