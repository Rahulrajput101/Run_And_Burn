package com.androiddevs.runandburn.service

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location

import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.utlis.Constants.ACTION_PAUSE_SERVICE
import com.androiddevs.runandburn.utlis.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.androiddevs.runandburn.utlis.Constants.ACTION_START_OR_RESUME
import com.androiddevs.runandburn.utlis.Constants.ACTION_STOP_SERVICE
import com.androiddevs.runandburn.utlis.Constants.FASTEST_LOCATION_INTERVAL
import com.androiddevs.runandburn.utlis.Constants.LOCATION_UPDATE_INTERVAL
import com.androiddevs.runandburn.utlis.Constants.NOTIFICATION_CHANNEL_NAME
import com.androiddevs.runandburn.utlis.Constants.NOTIFICATION_CHANNNEL_ID
import com.androiddevs.runandburn.utlis.Constants.NOTIFICATION_ID
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    companion object{
        var isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()

    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
       fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
         when( it.action){
             ACTION_START_OR_RESUME -> {
                if(isFirstRun){
                    startForegroundservice()
                    isFirstRun = false
                }
             }
             ACTION_PAUSE_SERVICE-> Timber.d("ACTION_pause")

             ACTION_STOP_SERVICE -> Timber.d("ACTION_stop")
         }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking : Boolean ){
     if(isTracking){
         if(TrackingUtility.hasLocationPermission(this)){
           val request = LocationRequest.Builder(
               Priority.PRIORITY_HIGH_ACCURACY,
               LOCATION_UPDATE_INTERVAL).apply {
               setDurationMillis(LOCATION_UPDATE_INTERVAL)
               setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
               setWaitForAccurateLocation(true)
               setMaxUpdateAgeMillis(FASTEST_LOCATION_INTERVAL)
              }.build()

//             val request = LocationRequest().apply {
//                 interval = LOCATION_UPDATE_INTERVAL
//                 fastestInterval = FASTEST_LOCATION_INTERVAL
//                 priority = PRIORITY_HIGH_ACCURACY
//             }
//             val request = LocationRequest.create()
//                 .setInterval(LOCATION_UPDATE_INTERVAL)
//                 .setFastestInterval(FASTEST_LOCATION_INTERVAL)
//                 .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
             fusedLocationProviderClient.requestLocationUpdates(
                 request,
                 locationCallback,
                 Looper.getMainLooper())
         }
     } else{
         fusedLocationProviderClient.removeLocationUpdates(locationCallback)
     }
    }



    private val locationCallback = object : LocationCallback(){

        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
          if(isTracking.value!!){
              result.locations.let { locations ->
                  for(location in locations){
                      addLocation(location)
                      Timber.d("New Location is : ${location.latitude} and ${location.longitude}" )

                  }
              }
          }
        }
    }

    private fun addLocation(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }


    private fun addEmptyList() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun startForegroundservice(){
        addEmptyList()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE ) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createchannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_directions_run_24_black)
            .setContentTitle("Run and Burn")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingInetnt())
        startForeground(NOTIFICATION_ID,notificationBuilder.build())

    }



    private fun getMainActivityPendingInetnt() =PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createchannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNNEL_ID, NOTIFICATION_CHANNEL_NAME,
          IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}