package com.androiddevs.runandburn.repositary

import com.androiddevs.runandburn.db.Run
import com.androiddevs.runandburn.db.RunDAO
import javax.inject.Inject

class Repositary @Inject constructor(
    val runDao: RunDAO
) {

    suspend fun insertRun(run : Run)  = runDao.insert(run)
    suspend fun deleteRun(run :Run) = runDao.delete(run)

    fun getAllRunSortedByDates() = runDao.getAllSortedByDates()
    fun getAllRunSortedByTimeInMillis() = runDao.getAllSortedByTimeInMillis()
    fun getAllRunSortedByAvgSpeed() = runDao.getAllSortedByAvgSpeed()
    fun getAllRunSortedByDistance() = runDao.getAllSortedByDistance()
    fun getAllRunSortedByCaloriesBurned() = runDao.getAllSortedByCaloriesBurned()

    fun getTotalDistance() =runDao.getTotalDistance()
    fun getAvgSpeed() = runDao.getTotalAvgSpeed()
    fun getTotaltime() = runDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurend() =runDao.getTotalCaloriesBurned()
}