package com.androiddevs.runandburn.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.utlis.Constants.NOTIFICATION_CHANNEL_NAME
import com.androiddevs.runandburn.utlis.Constants.NOTIFICATION_CHANNNEL_ID
import com.androiddevs.runandburn.utlis.Constants.NOTIFICATION_ID
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {


    @Inject
    lateinit var notificationBuilder : NotificationCompat.Builder


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {

        if(message.notification != null){
            generateNotification(message.notification!!.title!!,message.notification!!.body!!)
        }
    }

    private fun generateNotification(title : String, message : String){

        notificationBuilder = notificationBuilder.apply {
            setContentText(message)
            setContentTitle(title)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotification = notificationManager.activeNotifications
        var isNotificationRunning = false

        for(activeNotification in activeNotification){
            if(activeNotification.id == NOTIFICATION_ID){
                isNotificationRunning = true
                break
            }

        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNNEL_ID, NOTIFICATION_CHANNEL_NAME
                , NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        if(!isNotificationRunning){
            notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
        }



    }

//    @SuppressLint("RemoteViewLayout")
//    private fun RemoteViews(title: String, message: String): RemoteViews {
//        val remoteView =RemoteViews("com.androiddevs.runandburn", R.layout.notify_layout)
//        remoteView.setTextViewText(R.id.title_text,title)
//        remoteView.setTextViewText(R.id.message,message)
//        remoteView.setImageViewResource(R.id.imageView,R.drawable.ic_baseline_notifications_active_24)
//        return remoteView
//    }


}