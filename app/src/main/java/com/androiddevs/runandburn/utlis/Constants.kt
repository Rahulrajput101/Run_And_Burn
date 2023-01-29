package com.androiddevs.runandburn.utlis

import android.graphics.Color
import com.androiddevs.runandburn.R
import pub.devrel.easypermissions.EasyPermissions
import java.security.AllPermission

object Constants {

    const val RUNNING_DATABASE_NAME ="running_db"
    const val REQUEST_CODE_LOCATION_PERMISSION =0

    const val ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT ="ACTION_SHOW_TRACKING_FRAGMENT "

    const val NOTIFICATION_CHANNEL_NAME ="Tracking"
    const val NOTIFICATION_CHANNNEL_ID ="tracking_channel"
    const val NOTIFICATION_ID =1

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.BLUE
    const val POLYLINE_WIDTH = 8F
    const val ZOOM_TO = 14f

    const val TIME_UPDATE_INTERVAL = 50L

    const val SHARED_PREFERENCE_NAME ="shared_pref"
    const val  KEY_FIRST_TIME = "KEY_FIRST_TIME"
    const val KEY_NAME = " KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"

    const val KEY_ONLY_ONE_TIME = "KEY_ONLY_ONE_TIME"


}