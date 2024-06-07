package com.msc.ar_drawing.component.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.domain.usecase.GetListFavouriteUseCase
import com.msc.ar_drawing.domain.usecase.UnFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val getListFavouriteUseCase: GetListFavouriteUseCase,
    private val removeFavouriteUseCase: UnFavouriteUseCase
): ViewModel() {

    val listFavouriteLive = MutableLiveData<List<DetailsSound>>()

    fun getListFavourite(){
        viewModelScope.launch(Dispatchers.IO){
            listFavouriteLive.postValue(getListFavouriteUseCase.execute(GetListFavouriteUseCase.Param()))
        }
    }

    fun unFavourite(it: DetailsSound) {
        viewModelScope.launch(Dispatchers.IO) {
            it.isFavourite = false
            removeFavouriteUseCase.execute(UnFavouriteUseCase.Param(it))
        }
    }
}