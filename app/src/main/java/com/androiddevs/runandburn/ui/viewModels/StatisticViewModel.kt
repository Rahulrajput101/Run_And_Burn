package com.androiddevs.runandburn.ui.viewModels

import androidx.lifecycle.ViewModel
import com.androiddevs.runandburn.repositary.Repositary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
  val  repositary: Repositary
) : ViewModel() {


   val totalDistance = repositary.getTotalDistance()
    val totalTime = repositary.getTotaltime()
    val totalAvgSpeed = repositary.getAvgSpeed()
    val  totalCaloriesBurned = repositary.getTotalCaloriesBurend()

    val runSortedByDate = repositary.getAllRunSortedByDates()





}