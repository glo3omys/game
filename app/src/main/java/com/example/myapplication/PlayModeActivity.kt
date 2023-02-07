package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPlayModeBinding

class PlayModeActivity : AppCompatActivity() {
    private var mBinding: ActivityPlayModeBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPlayModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceUtil(applicationContext)

        binding.tvMyId.text = "ID: ${prefs.getSharedPrefs("myID", "")}"

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