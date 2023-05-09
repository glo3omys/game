package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.ceil

class GameHelpDialog(context: LobbyActivity){
    var mContext = context
    var mBuilder = AlertDialog.Builder(mContext)
    lateinit var mDialogView: View
    lateinit var mAlertDialog: AlertDialog
    lateinit var nextIntent: Intent

    //private var mBinding: ViewpagerGameHelpBinding? = null
    //private val binding get() = mBinding!!

    var datas = mutableListOf<GameHelpData>()
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var vpGameList: ViewPager2
    lateinit var vpTabLay: TabLayout
    var currentPos = -1

    fun gameHelp() {
        //val tabTextList = listOf("TapTap", "Balloon")
        val tabIconList = R.drawable.help_icon

        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_game_help, null)
        vpGameList = mDialogView.findViewById(R.id.vp_game_list)
        vpTabLay = mDialogView.findViewById(R.id.vp_tab_lay)

        currentPos = Int.MAX_VALUE / 2 - ceil(datas.size.toDouble() / 2).toInt()
        setDatas()
        initViewForHorizontal()


        TabLayoutMediator(vpTabLay, vpGameList) { tab, pos ->
            // tab.text = tabTextList[pos]
            tab.setIcon(tabIconList)
        }.attach()

        vpGameList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            /*var currentState = 0
            var currentPos = 0

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (currentState == ViewPager2.SCROLL_STATE_DRAGGING && currentPos == position) {
                    if (currentPos == 0)
                        vpGameList.currentItem = lastIndex
                    else if (currentPos == lastIndex)
                        vpGameList.currentItem = 0
                }
            }
            */
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPos = position
            }
            /*
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                currentState = state
            }*/
        })

        mBuilder.setView(mDialogView)
            .setTitle("")
        mAlertDialog =  mBuilder.create()
        mAlertDialog.show()

        // ok button listener
        val okButton = mDialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }

    private fun setDatas() {
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
        datas.add(GameHelpData("탭탭", description))
        datas.add(GameHelpData("풍선 터뜨리기", description))
        datas.add(GameHelpData("숫자 찾기", description))
        datas.add(GameHelpData("사칙연산", description))
        datas.add(GameHelpData("좌로 우로", description))
        datas.add(GameHelpData("짝 찾아 뒤집기", description))
        datas.add(GameHelpData("두더지 잡기", description))
        datas.add(GameHelpData("초성 퀴즈", description))
    }

    private fun initViewForHorizontal() {
        if (datas.size > 0) {
            viewPagerAdapter = ViewPagerAdapter()
            vpGameList.adapter = viewPagerAdapter
            viewPagerAdapter.setSliderList(mContext, vpGameList, datas)
        }
    }
}