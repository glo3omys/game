package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameListActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)



        val findroombtn = findViewById<Button>(R.id.find_room)
        findroombtn.setOnClickListener {
            val nextIntent = Intent(this, SearchRoomActivity::class.java)
            startActivity(nextIntent)
            /*val dialog = MyDialog(this)
            dialog.createRoom("master")
            dialog.findRoom()*/
        }

        val testbtn = findViewById<Button>(R.id.testbtn)
        testbtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("test")
        }

        val tapbtn = findViewById<Button>(R.id.goto_taptap)
        tapbtn.setOnClickListener() {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "Taptap")
        }
        val balloonbtn = findViewById<Button>(R.id.goto_balloon)
        balloonbtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "Balloon")
        }
        val scavbtn = findViewById<Button>(R.id.goto_scav)
        scavbtn.setOnClickListener {
            val nextIntent = Intent(this, ScavengerHuntActivity::class.java)
            startActivity(nextIntent)
        }
        val mathbtn = findViewById<Button>(R.id.goto_math)
        mathbtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "Math")
        }
        val choicebtn = findViewById<Button>(R.id.goto_choice)
        choicebtn.setOnClickListener {
            val nextIntent = Intent(this, FingerChoiceActivity::class.java)
            //Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()
            startActivity(nextIntent)
        }
        val initialbtn = findViewById<Button>(R.id.goto_initial)
        initialbtn.setOnClickListener {
            val nextIntent = Intent(this, InitialQuizActivity::class.java)
            //Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()
            startActivity(nextIntent)
        }
        val leftrightbtn = findViewById<Button>(R.id.goto_leftright)
        leftrightbtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "LeftRight")
        }
        val findnumbtn = findViewById<Button>(R.id.goto_findnum)
        findnumbtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "FindNumber")
        }
        val conntestbtn = findViewById<Button>(R.id.goto_conntest)
        conntestbtn.setOnClickListener {
            val nextIntent = Intent(this, ConnTestActivity::class.java)
            //Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()
            startActivity(nextIntent)
        }
        val wambtn = findViewById<Button>(R.id.goto_wam)
        wambtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "WhackAMole")
        }
        val memorybtn = findViewById<Button>(R.id.goto_memory)
        memorybtn.setOnClickListener {
            val dialog = MyDialog(this)
            dialog.myDig("game description", "MemoryCardGame")
        }
    }
}