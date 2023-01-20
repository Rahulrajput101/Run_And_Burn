package com.androiddevs.runandburn.ui.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.runandburn.db.Run
import com.androiddevs.runandburn.repositary.Repositary
import com.androiddevs.runandburn.utlis.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
  val mainRepositary: Repositary
) : ViewModel(){



  private val setRunAdapterByDate = mainRepositary.getAllRunSortedByDates()
  private val setRunAdapterByAvgSpeed =mainRepositary.getAllRunSortedByAvgSpeed()
  private val setRunAdapterByDistance =mainRepositary.getAllRunSortedByDistance()
  private val setRunAdapterByTime =mainRepositary.getAllRunSortedByTimeInMillis()
  private val setRunAdapterByCaloriesBurned =mainRepositary.getAllRunSortedByCaloriesBurned()


  val run = MediatorLiveData<List<Run>>()

  var sortType = SortType.DATE


  init {

    //ByDate
    run.addSource(setRunAdapterByDate){ result ->

      if(sortType == SortType.DATE){
        result?.let {
          run.value = it
        }
      }


    }

    //BySpeed
    run.addSource(setRunAdapterByAvgSpeed){ result ->
      if(sortType == SortType.AVG_SPEED){
        result?.let {
          run.value = it
        }
      }

    }

    //ByDistance
    run.addSource(setRunAdapterByDistance){ result ->
      if(sortType == SortType.DISTANCE){
        result?.let {
          run.value = it
        }
      }

    }

    //ByTime
    run.addSource(setRunAdapterByTime){ result ->
      if(sortType == SortType.TIME){
        result?.let {
          run.value = it
        }
      }

    }

    //ByCalories
    run.addSource(setRunAdapterByCaloriesBurned){ result ->
      if(sortType == SortType.CALORIES_BURNED){
        result?.let {
          run.value = it
        }
      }
    }



  }

  fun sortRun( sortType: SortType) = when(sortType){
    SortType.DATE -> setRunAdapterByDate.value?.let{ run.value = it}
    SortType.AVG_SPEED -> setRunAdapterByAvgSpeed.value?.let{ run.value = it}
    SortType.DISTANCE -> setRunAdapterByDistance.value?.let{ run.value = it}
    SortType.TIME -> setRunAdapterByTime.value?.let{ run.value = it}
    SortType.CALORIES_BURNED -> setRunAdapterByCaloriesBurned.value?.let{ run.value = it}

  }.also {
    this.sortType = sortType
  }


  fun insert(run : Run) = viewModelScope.launch {
    mainRepositary.insertRun(run)
  }


}