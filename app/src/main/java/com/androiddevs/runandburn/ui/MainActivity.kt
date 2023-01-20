package com.androiddevs.runandburn.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.ActivityMainBinding
import com.androiddevs.runandburn.db.RunDAO
import com.androiddevs.runandburn.utlis.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
     private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


         navigateToTracking(intent)
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navHostFragment.findNavController()
            .addOnDestinationChangedListener{_, destination, _ ->
                when(destination.id) {
                    R.id.settingFragment,R.id.runFragment,R.id.statisticFragment->
                        binding.bottomNavigationView.visibility = View.VISIBLE

                    else ->binding.bottomNavigationView.visibility = View.GONE
                }

            }


        setContentView(binding.root)



    }
    fun textToolbar(toolbartext : String) {
         binding.tvToolbarTitle.text = toolbartext

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTracking(intent)
    }

    private fun navigateToTracking(intent: Intent?){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)


        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_tracking_fragment)
        }
    }
}