package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.GameHelpBalloonBinding
import com.example.myapplication.databinding.GameHelpInitialQuizBinding
import com.example.myapplication.databinding.GameHelpWhackBinding

class PageInitial : Fragment() {
    private var mBinding: GameHelpInitialQuizBinding?= null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = GameHelpInitialQuizBinding.inflate(layoutInflater)

        //return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}