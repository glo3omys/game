package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tapbtn = findViewById<Button>(R.id.goto_taptap)
        tapbtn.setOnClickListener {
            val nextIntent = Intent(this, TaptapActivity::class.java)
            startActivity(nextIntent)
        }
        val balloonbtn = findViewById<Button>(R.id.goto_balloon)
        balloonbtn.setOnClickListener {
            val nextIntent = Intent(this, BalloonActivity::class.java)
            startActivity(nextIntent)
        }
        val scavbtn = findViewById<Button>(R.id.goto_scav)
        scavbtn.setOnClickListener {
            val nextIntent = Intent(this, ScavengerHuntActivity::class.java)
            startActivity(nextIntent)
        }
        val mathbtn = findViewById<Button>(R.id.goto_math)
        mathbtn.setOnClickListener {
            val nextIntent = Intent(this, MathActivity::class.java)
            //Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()
            startActivity(nextIntent)
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
    }
}