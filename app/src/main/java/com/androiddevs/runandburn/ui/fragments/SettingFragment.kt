package com.androiddevs.runandburn.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.databinding.FragmentSettingBinding
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.utlis.Constants.KEY_NAME
import com.androiddevs.runandburn.utlis.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater)
        loadDetails()


        binding.applyChanges.setOnClickListener {
            val successfullyChange = changeDetail()

            if(successfullyChange){
                Snackbar.make(requireView(),"Successfully changed the details",Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(requireView(),"Please enter the all fields",Snackbar.LENGTH_SHORT).show()
            }
        }



        return binding.root
    }

    private fun loadDetails() {
        val name = sharedPreferences.getString(KEY_NAME,"") ?: ""
        val weight = sharedPreferences.getFloat(KEY_WEIGHT,60f)
        binding.changeName.setText(name)
        binding.changeWeight.setText(weight.toString())
    }


    private fun changeDetail() : Boolean{
        val name = binding.changeName.text.toString()
        val weight = binding.changeWeight.text.toString()

        if(name.isEmpty() && weight.isEmpty()){
               return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .apply()

        val activity = activity as MainActivity
        activity.textToolbar(name)
        Toast.makeText(requireContext()," $name",Toast.LENGTH_LONG).show()

        return true



    }



}