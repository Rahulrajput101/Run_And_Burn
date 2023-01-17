package com.androiddevs.runandburn.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.runandburn.db.Run
import com.androiddevs.runandburn.repositary.Repositary
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
  val mainRepositary: Repositary
) : ViewModel(){



  fun setRunAdapterByDate() = mainRepositary.getAllRunSortedByDates()


  fun insert(run : Run) = viewModelScope.launch {
    mainRepositary.insertRun(run)
  }


}