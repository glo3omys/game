package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.ViewpagerGameHelpBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GameHelpDialog(context: LobbyActivity){
    var mContext = context
    var mBuilder = AlertDialog.Builder(mContext)
    lateinit var mDialogView: View
    lateinit var mAlertDialog: AlertDialog
    lateinit var nextIntent: Intent

    //private var mBinding: ViewpagerGameHelpBinding? = null
    //private val binding get() = mBinding!!

    fun gameHelp() {
        //val tabTextList = listOf("TapTap", "Balloon")
        val tabIconList = R.drawable.help_icon

        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_game_help, null)
        val vpGameList = mDialogView.findViewById<ViewPager2>(R.id.vp_game_list)
        val vpTabLay = mDialogView.findViewById<TabLayout>(R.id.vp_tab_lay)

        vpGameList.adapter = ViewPagerAdapter(mContext as FragmentActivity)
        TabLayoutMediator(vpTabLay, vpGameList) { tab, pos ->
            // tab.text = tabTextList[pos]
            tab.setIcon(tabIconList)
        }.attach()

        mBuilder.setView(mDialogView)
            .setTitle("")
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        // ok button listener
    }
}