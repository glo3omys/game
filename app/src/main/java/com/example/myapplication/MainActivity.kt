package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var prefs : PreferenceUtil
    //private val prefs = PreferenceUtil(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferenceUtil(applicationContext)

        binding.etId.addTextChangedListener(object: TextWatcher {
            val etId = binding.etId
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val res = s.toString().replace(" ", "")
                if (!s.toString().equals(res)) {
                    etId.setText(res)
                    etId.setSelection(res.length)
                }
                if (!s.toString().equals("") && s.toString()[s!!.length - 1].toString() == "\n") {
                    val res = s.toString().replace("\n", "")
                    if (!s.toString().equals(res)) {
                        etId.setText(res)
                        etId.setSelection(res.length)
                    }
                    if (res.equals(""))
                        return
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etId.windowToken, 0)

                    setID()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(s: Editable?) { }
        })
        binding.btnOk.setOnClickListener() {
            val etId = binding.etId
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etId.windowToken, 0)
            setID()
        }
        getID()
    }
    private fun setID() {
        val newID = binding.etId.text.toString()
        prefs.setSharedPrefs("myID", newID)

        finish()
        val nextIntent = Intent(this, PlayModeActivity::class.java)
        startActivity(nextIntent)
    }
    private fun getID() {
        val myID = prefs.getSharedPrefs("myID", "")
        if (myID != "") {
            finish()
            val nextIntent = Intent(this, PlayModeActivity::class.java)
            startActivity(nextIntent)
        }
    }
}