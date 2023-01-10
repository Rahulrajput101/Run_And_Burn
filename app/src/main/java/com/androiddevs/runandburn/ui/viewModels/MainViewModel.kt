package com.androiddevs.runandburn.ui.viewModels

import androidx.lifecycle.ViewModel
import com.androiddevs.runandburn.repositary.Repositary
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

class MainViewModel @ViewModelScoped constructor(
  val mainRepositary: Repositary
) : ViewModel(){



}