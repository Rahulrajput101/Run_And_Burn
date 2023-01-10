package com.androiddevs.runandburn.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.databinding.FragmentStatisticBinding


class StatisticFragment : Fragment() {

    private lateinit var binding : FragmentStatisticBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatisticBinding.inflate(inflater)
        return binding.root
    }

}