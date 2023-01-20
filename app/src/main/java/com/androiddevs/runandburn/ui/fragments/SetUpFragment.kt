package com.androiddevs.runandburn.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.ActivityMainBinding
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.databinding.FragmentSetUpBinding
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.utlis.Constants.KEY_FIRST_TIME
import com.androiddevs.runandburn.utlis.Constants.KEY_NAME
import com.androiddevs.runandburn.utlis.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment : Fragment() {

    private lateinit var binding: FragmentSetUpBinding
    //private lateinit var activity: MainActivity

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var firstAttempt = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSetUpBinding.inflate(inflater)
        //activity = MainActivity()

        if(!firstAttempt){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setUpFragment, true)
                .build()
            findNavController().navigate(
                //SetUpFragmentDirections.actionSetUpFragmentToRunFragment(),
                R.id.action_setUpFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )


        }



            binding.tvContinue.setOnClickListener {
                val success = getPersonalDetailforSharedPref()
                if(success){
                    findNavController().navigate(SetUpFragmentDirections.actionSetUpFragmentToRunFragment())
                }else{
                    Snackbar.make(requireView(), "Please enter the al fileds", Snackbar.LENGTH_SHORT).show()
                }


            }









        return binding.root
    }

    private fun getPersonalDetailforSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() && weight.isEmpty()) {
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME, false)
            .apply()

        val toolbarText = " Let's go $name!"
        val act = activity as MainActivity
        //requireActivity().findViewById(R.id.tvToolbarTitle) = toolbarText
        act.textToolbar(toolbarText)
//        val activity =requireActivity()
//        activity.findViewById(R.id.tvToolbarTitle)
        return true

    }
}



