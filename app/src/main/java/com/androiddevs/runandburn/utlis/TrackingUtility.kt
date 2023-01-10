package com.androiddevs.runandburn.utlis

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {


    fun  hasLocationPermission(context : Context) : Boolean= if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
        EasyPermissions.hasPermissions(context,
           android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,

            )


    } else {
        EasyPermissions.hasPermissions(context,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )
    }

    fun hasNotifcationPermission(context: Context) : Boolean = if (Build.VERSION.SDK_INT> Build.VERSION_CODES.S_V2){
        EasyPermissions.hasPermissions(context,
        android.Manifest.permission.POST_NOTIFICATIONS)
    }
    else{
        false
    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun requestNotificationPermission(context: Context) : Boolean {
//
//        val b = ActivityCompat.checkSelfPermission(
//           context,
//            Manifest.permission.POST_NOTIFICATIONS
//        ) == PackageManager.PERMISSION_GRANTED
//        return b
//
//
//    }

}