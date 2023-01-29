package com.androiddevs.runandburn.utlis

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.service.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import kotlin.math.min

object TrackingUtility {


    fun fLocationPermission(context: Context) =
        ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    fun cLocationPermission(context: Context) =
        ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun notificationPermission(context: Context) =
        ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED







    fun  hasLocationPermission(context : Context) : Boolean  =
        EasyPermissions.hasPermissions(context,
           android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,

            )


    //if (Build.VERSION.SDK_INT> Build.VERSION_CODES.S_V2)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasNotifcationPermission(context: Context) : Boolean = EasyPermissions.hasPermissions(context,
        android.Manifest.permission.POST_NOTIFICATIONS)






    fun reqIngnoreBatterConsumption(context: Context?) : Boolean {
        val packageName = context?.packageName
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)){
            return false
        }
        return true
    }

    fun getformattedStopWatchTime( ms :  Long , includeMillis : Boolean =false) : String{

        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if(!includeMillis){
            return "${if (hours<10) "0" else ""}$hours:" +
                    "${if(minutes<10) "0" else ""}$minutes:" +
                    "${if(seconds<10) "0" else ""}$seconds"


        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /=10
        return "${if (hours<10) "0" else ""}$hours:" +
               "${if(minutes<10) "0" else ""}$minutes:" +
                "${if(seconds<10) "0" else ""}$seconds:" +
                "${if(milliseconds<10) "0" else ""}$milliseconds"
    }

    fun calculatePolylineLength(polyline: Polyline) : Float{
        var distance = 0f
        for(i in 0..polyline.size-2){
            val pos1 =polyline[i]
            val pos2 =polyline[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result

            )
            distance +=result[0]
        }
        return distance

    }

    fun gpsIsOnOrOff(context: Context) : Boolean{

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(!isGpsEnabled && !isNetworkEnabled) {
           return false
//            val gpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(gpsIntent)
        }
        return true
    }

    fun colorList(context: Context) : List<Int>{
        return listOf(
            ContextCompat.getColor(context, R.color.pink22),
            ContextCompat.getColor(context, R.color.lightGreen22),
            ContextCompat.getColor(context, R.color.purple_200),
            ContextCompat.getColor(context, R.color.beige22),
            ContextCompat.getColor(context, R.color.teal_200),
            ContextCompat.getColor(context, R.color.md_blue_400)
        )
    }

}
