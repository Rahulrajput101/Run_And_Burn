package com.androiddevs.runandburn.utlis

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.ItemMarkerViewBinding
import com.androiddevs.runandburn.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*


class CustomMarkerView(
    val runs : List<Run>,
    context : Context,
    layoutId : Int
    ) : MarkerView(context,layoutId){


    private var dateCard: TextView = findViewById(R.id.cardViewDate)
    private var avgSpeedCard: TextView = findViewById(R.id.cardViewAvgSpeed)
    private var distanceCard: TextView = findViewById(R.id.cardViewDistance)
    private var timeCard: TextView = findViewById(R.id.cardViewTime)
    private var caloriesBurnedCard: TextView? = null
 init {
     caloriesBurnedCard = findViewById(R.id.calories_text)

 }
    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)


        if(e == null){
            return
        }
        val currentPosId = e.x.toInt()
        val run =runs[currentPosId]



        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
         dateCard.text=dateFormat.format(calendar.time)
        val avgSpeed ="${run.avgSpeedInKMH}km/h"
        avgSpeedCard.text = avgSpeed

        val distanceInKm = "${run.distanceInMeters/1000f}km"
        distanceCard.text = distanceInKm

        val caloriesBurn = "${run.caloriesBurned}kcal"
        caloriesBurnedCard?.text = caloriesBurn
        val time = TrackingUtility.getformattedStopWatchTime(run.timeInMillis)
        timeCard.text =time
    }
}