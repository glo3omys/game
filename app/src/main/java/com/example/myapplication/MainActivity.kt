package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityFingerChoiceBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityMathBinding

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlayAlone.setOnClickListener() {
            val nextIntent = Intent(this, GameListActivity::class.java)
            startActivity(nextIntent)
        }

        binding.btnPlayTogether.setOnClickListener() {
            val nextIntent = Intent(this, SearchRoomActivity::class.java)
            startActivity(nextIntent)
        }

    }
}