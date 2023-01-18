 package com.androiddevs.runandburn.service

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
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
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.androiddevs.runandburn.utlis.Constants.TIME_UPDATE_INTERVAL
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

 typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

 @AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true
     var serviceKilled = false
     @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder : NotificationCompat.Builder

    lateinit var  currentNotificationBuilder : NotificationCompat.Builder

    private val timeRunInSeconds = MutableLiveData<Long>()


    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()

    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInMillis.postValue(0L)
        timeRunInSeconds.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()
       fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationState(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
         when( it.action){
             ACTION_START_OR_RESUME -> {
                if(isFirstRun){
                    startForegroundservice()
                    isFirstRun = false
                } else{
                  startTimer()
                }

             }
             ACTION_PAUSE_SERVICE-> {
                 Timber.d("ACTION_pause")
                 pauseService()
             }

             ACTION_STOP_SERVICE -> {
                 killService()
                 Timber.d("ACTION_stop")
             }
         }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer(){
        addEmptyList()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                //Time difference btw now and started
                lapTime = System.currentTimeMillis() - timeStarted
                //Post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimeStamp +1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIME_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }



    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }



     private fun killService(){
         serviceKilled=true
         isFirstRun = true
         pauseService()
         postInitialValues()
         stopForeground(STOP_FOREGROUND_DETACH)
         stopSelf()
     }




     private fun updateNotificationState(isTracking: Boolean){

         val notificationActionText = if(isTracking) "Pause" else "Resume"
         val pendingIntent = if(isTracking){
             val pauseIntent = Intent(this,TrackingService::class.java).apply {
                 action = ACTION_PAUSE_SERVICE
             }
             PendingIntent.getService(this,1,pauseIntent,FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
         } else{
             val resumeIntent = Intent(this,TrackingService::class.java).apply {
                 action = ACTION_START_OR_RESUME
             }
             PendingIntent.getService(this,2,resumeIntent,FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
         }

          val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

         currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
             isAccessible =true
             set(currentNotificationBuilder,java.util.ArrayList<NotificationCompat.Action>())
         }

         if (!serviceKilled){
             currentNotificationBuilder =baseNotificationBuilder
                 .addAction(R.drawable.ic_baseline_pause_24,notificationActionText,pendingIntent)

             notificationManager.notify(NOTIFICATION_ID,currentNotificationBuilder.build())
         }




     }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking : Boolean ){
     if(isTracking){
         if(TrackingUtility.hasLocationPermission(this)){
           val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL).apply {
               setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
               setWaitForAccurateLocation(true)
               setMaxUpdateAgeMillis(FASTEST_LOCATION_INTERVAL)
              }.build()

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
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE ) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createchannel(notificationManager)
        }

//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNNEL_ID)
//            .setAutoCancel(false)
//            .setOngoing(true)
//            .setSmallIcon(R.drawable.ic_baseline_directions_run_24_black)
//            .setContentTitle("Run and Burn")
//            .setContentText("00:00:00")
//            .setContentIntent(getMainActivityPendingInetnt())
        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            if(!serviceKilled){
                val notification = currentNotificationBuilder
                    .setContentText(TrackingUtility.getformattedStopWatchTime(it*1000))

                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }

        })


    }



//    private fun getMainActivityPendingInetnt() =PendingIntent.getActivity(
//        this,
//        0,
//        Intent(this,MainActivity::class.java).also {
//            it.action = ACTION_SHOW_TRACKING_FRAGMENT
//        },
//        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
//    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createchannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNNEL_ID, NOTIFICATION_CHANNEL_NAME,
          IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}