package com.androiddevs.runandburn.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationProvider
import androidx.core.app.NotificationCompat
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.utlis.Constants
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
  @Provides
  fun provideFusedLocationProviderClient(
      @ApplicationContext app: Context
  ) = LocationServices.getFusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
   fun provideMainActivityPendingIntent(
       @ApplicationContext app: Context
   ) = PendingIntent.getActivity(
       app,
       0,
       Intent(app, MainActivity::class.java).also {
           it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
       },
       PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
   )

    @ServiceScoped
    @Provides
    fun provideBasicNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_baseline_directions_run_24_black)
        .setContentTitle("Run and Burn")
        .setContentText("00:00:00")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setShowWhen(true)
        .setContentIntent(pendingIntent)




}