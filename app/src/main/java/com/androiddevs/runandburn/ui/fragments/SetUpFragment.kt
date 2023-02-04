package com.androiddevs.runandburn.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.ActivityMainBinding
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.databinding.FragmentSetUpBinding
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.utlis.Constants.KEY_FIRST_TIME
import com.androiddevs.runandburn.utlis.Constants.KEY_NAME
import com.androiddevs.runandburn.utlis.Constants.KEY_ONLY_ONE_TIME
import com.androiddevs.runandburn.utlis.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment : Fragment() {

    private lateinit var binding: FragmentSetUpBinding


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

        val manufactruer = Build.MANUFACTURER

        val sharedPref = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val hasRun = sharedPref?.getBoolean("hasRun", false)

        if (!hasRun!!) {
            // Run the method here
            if (manufactruer == "vivo") {
                ignoreBattery()
            }
            val editor = sharedPref.edit()
            editor.putBoolean("hasRun", true)
            editor.apply()
        }


        if (!firstAttempt) {
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
            if (success) {
                findNavController().navigate(SetUpFragmentDirections.actionSetUpFragmentToRunFragment())
            } else {
                Snackbar.make(requireView(), "Please enter the all fileds", Snackbar.LENGTH_SHORT)
                    .show()
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

        val act = activity as MainActivity
        act.textToolbar(name)
        return true

    }

    fun ignoreBattery() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(" Don't Ignore")
        dialog.setMessage("To ensure this app will work go to, Battery settings -> Background Power Consumption -> Run and Burn ,and select the option of Don't restrict.")
        dialog.setPositiveButton("Go to settings") { _, _ ->
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
        dialog.setNegativeButton("cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        dialog.create()
        dialog.show()

        sharedPreferences.edit()
            .putBoolean(KEY_ONLY_ONE_TIME, true)
            .apply()
    }

}



