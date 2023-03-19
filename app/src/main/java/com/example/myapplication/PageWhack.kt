package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.GameHelpBalloonBinding
import com.example.myapplication.databinding.GameHelpWhackBinding

class PageWhack : Fragment() {
    private var mBinding: GameHelpWhackBinding?= null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = GameHelpWhackBinding.inflate(layoutInflater)

        //return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}