package com.androiddevs.runandburn.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.databinding.FragmentStatisticBinding
import com.androiddevs.runandburn.ui.viewModels.StatisticViewModel
import com.androiddevs.runandburn.utlis.CustomMarkerView
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round
import kotlin.random.Random

@AndroidEntryPoint
class StatisticFragment : Fragment() {

    private lateinit var binding : FragmentStatisticBinding

    private  val viewModel : StatisticViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatisticBinding.inflate(inflater)


        subscribeToObserver()

        setUpBarGraph()
        return binding.root
    }

    private fun setUpBarGraph() {
      binding.BarGraph.xAxis.apply {
          position = XAxis.XAxisPosition.BOTTOM
          setDrawLabels(false)
          axisLineColor = Color.BLACK
          textColor =Color.BLACK
          setDrawGridLines(false)
      }
        binding.BarGraph.axisLeft.apply {
            axisLineColor = Color.BLACK
            axisMinimum = 0f
            textColor = Color.BLACK
            setDrawGridLines(false)
        }

        binding.BarGraph.axisRight.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }

        binding.BarGraph.apply {
            description.text = " Avg speed over time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObserver() {

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let{
                val distanceInKM = it / 1000f
                val totalDistance = round(distanceInKM*10)/10
                val distanceInString = "${totalDistance}km"
                binding.totalDistance.text = distanceInString
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
               val averageSpeed = round(it *10)/10
                val averageSpeedString = "${averageSpeed}km/h"
                binding.totalAvgSpeed.text = averageSpeedString
            }
        })

        viewModel.totalTime.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTime = TrackingUtility.getformattedStopWatchTime(it)
                binding.totalTime.text =totalTime
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
            val totalCalories = "${it}kcl"
                binding.totalCaloriesBurned.text = totalCalories
            }
        })



        viewModel.runSortedByDate.observe(viewLifecycleOwner,Observer{
            it?.let{
                 val allAvgSpeed = it.indices.map { i-> BarEntry(i.toFloat(), it[i].avgSpeedInKMH)
                 }
                val barDataSet = BarDataSet(allAvgSpeed, "AvgSpeed over time",).apply {
                    valueTextColor = Color.BLACK
                }

                barDataSet.colors = TrackingUtility.colorList(requireContext()).shuffled()
                barDataSet.valueTextSize = 12f

                binding.BarGraph.data = BarData(barDataSet)
                binding.BarGraph.marker = CustomMarkerView(it,requireContext(),R.layout.item_marker_view)
                binding.BarGraph.animateXY(3000,3000)
                binding.BarGraph.invalidate()
            }
        })



    }


}