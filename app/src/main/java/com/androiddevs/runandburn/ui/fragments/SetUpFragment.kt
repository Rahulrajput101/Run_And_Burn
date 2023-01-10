package com.androiddevs.runandburn.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.databinding.FragmentSetUpBinding


class SetUpFragment : Fragment() {
    private lateinit var binding : FragmentSetUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetUpBinding.inflate(inflater)

        binding.tvContinue.setOnClickListener {
            findNavController().navigate(SetUpFragmentDirections.actionSetUpFragmentToRunFragment())
        }



        return binding.root
    }


}