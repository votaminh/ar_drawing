package com.msc.ar_drawing.component.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msc.ar_drawing.domain.usecase.GetListImageUseCase
import com.msc.ar_drawing.domain.usecase.GetListLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val getListImageUseCase: GetListImageUseCase
) : ViewModel() {

    val imagesPathLive = MutableLiveData<List<String>>()

    fun getListImage(){
        viewModelScope.launch {
            imagesPathLive.postValue(getListImageUseCase.execute(GetListImageUseCase.Param()))
        }
    }

}