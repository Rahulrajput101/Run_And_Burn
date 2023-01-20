package com.androiddevs.runandburn.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.adapter.RunAdapter
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.ui.viewModels.MainViewModel
import com.androiddevs.runandburn.utlis.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.androiddevs.runandburn.utlis.SortType
import com.androiddevs.runandburn.utlis.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter : RunAdapter

    @set:Inject
    var name  = ""

    //@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRunBinding.inflate(inflater)

        toolBarText()
        //ignoreBattery()
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            perm()
        }

        requestPermission()
        //reqNotification()

        adapter = RunAdapter()
        binding.rvRuns.adapter = adapter
        binding.rvRuns.layoutManager =LinearLayoutManager(context)

        when(viewModel.sortType){
            SortType.DATE -> binding.spinner.setSelection(0)
            SortType.AVG_SPEED -> binding.spinner.setSelection(1)
            SortType.DISTANCE -> binding.spinner.setSelection(2)
            SortType.TIME -> binding.spinner.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spinner.setSelection(4)


        }

        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos){
                    0 -> viewModel.sortRun(SortType.DATE)
                    1 -> viewModel.sortRun(SortType.AVG_SPEED)
                    2 -> viewModel.sortRun(SortType.DISTANCE)
                    3 -> viewModel.sortRun(SortType.TIME)
                    4 -> viewModel.sortRun(SortType.CALORIES_BURNED)

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        viewModel.run.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })



        binding.fab.setOnClickListener {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
        }




        return binding.root
    }
//    fun ignoreBattery(){
//        val intent = Intent()
//        val packageName = context?.packageName
//        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
//        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
//            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//            intent.data = Uri.parse("package:$packageName")
//            context?.startActivity(intent)
//        }
//    }











    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
    }


    private fun requestPermission() {
        if (!TrackingUtility.hasLocationPermission(requireContext())) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept the permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
        else{
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accept the permissionbbb to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accept the permission to use iiii this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                   // Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }


    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

   @RequiresApi(Build.VERSION_CODES.TIRAMISU)
   fun perm(){
       when {
          TrackingUtility.requestNotificationPermission(requireContext())-> {
               // You can use the API that requires the permission.
           }
           shouldShowRequestPermissionRationale(  Manifest.permission.POST_NOTIFICATIONS) -> {
               EasyPermissions.requestPermissions(
                   this,
                   "You need to accept the permission to use this app",
                   1,
                   Manifest.permission.POST_NOTIFICATIONS
               )
           }

           else -> {
               // You can directly ask for the permission.
               // The registered ActivityResultCallback gets the result of this request.
               pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
           }
       }
   }
    private fun toolBarText(){

        val toolbarName = "Let's go $name!"
        val activity = activity as MainActivity
        activity.textToolbar(toolbarName)
    }




}