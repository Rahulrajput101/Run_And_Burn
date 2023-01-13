package com.androiddevs.runandburn.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.ui.viewModels.MainViewModel
import com.androiddevs.runandburn.utlis.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.androiddevs.runandburn.utlis.TrackingUtility
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRunBinding.inflate(inflater)
        perm()
        requestPermission()
        //reqNotification()

        binding.fab.setOnClickListener {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
        }




        return binding.root
    }
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun reqNotification(){
//       when(!TrackingUtility.hasNotifcationPermission(requireContext())){
//          true-> pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//
//           shouldShowRequestPermissionRationale(  Manifest.permission.POST_NOTIFICATIONS) -> {
//               EasyPermissions.requestPermissions(
//                   this,
//                   "You need to accept the permission to use this app",
//                   1,
//                   Manifest.permission.POST_NOTIFICATIONS
//               )
//           }
//
//           else -> {
//               pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//           }
//       }
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



}