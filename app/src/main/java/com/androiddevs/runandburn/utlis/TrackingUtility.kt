package com.androiddevs.runandburn.utlis

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.androiddevs.runandburn.service.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import kotlin.math.min

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
           // android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )
    }

    fun hasNotifcationPermission(context: Context) : Boolean = if (Build.VERSION.SDK_INT> Build.VERSION_CODES.S_V2){
        EasyPermissions.hasPermissions(context,
        android.Manifest.permission.POST_NOTIFICATIONS)
    }
    else{
        false
     }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
   fun requestNotificationPermission(context: Context) : Boolean {

        return ActivityCompat.checkSelfPermission(
           context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

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

}
