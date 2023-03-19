package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.GameHelpBalloonBinding

class PageBalloon : Fragment() {
    private var mBinding: GameHelpBalloonBinding?= null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = GameHelpBalloonBinding.inflate(layoutInflater)

        //return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}