package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import scavDatas
import java.util.regex.Pattern

class CountDownDialog(context: Context){
    var mContext = context
    var mBuilder = AlertDialog.Builder(mContext)
    lateinit var mDialogView: View
    lateinit var mAlertDialog: AlertDialog

    fun countDown() {
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.count_down_dialog, null)
        val tvCountDown : TextView = mDialogView.findViewById(R.id.tv_count_down)

        mBuilder.setView(mDialogView)
            .setTitle("")
        mAlertDialog =  mBuilder.create()
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mAlertDialog.show()

        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var sec = (500 + millisUntilFinished) / 1000
                tvCountDown.text = sec.toString()
                //tvCountDown.text = millisUntilFinished.toString()
            }

            override fun onFinish() {
                //mContext.runTimer()
                mAlertDialog.dismiss()
            }
        }
        timer.start()
    }
}