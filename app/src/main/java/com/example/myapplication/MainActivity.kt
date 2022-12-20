package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
    }
}