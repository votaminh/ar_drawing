package com.msc.ar_drawing.component.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msc.ar_drawing.R
import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.domain.usecase.CheckIsFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context : Context,
    val isFavouriteUseCase: CheckIsFavouriteUseCase
    ) : ViewModel() {

    val detailsListLive = MutableLiveData<List<DetailsSound>>()

    fun getBestSound(){
        viewModelScope.launch(Dispatchers.IO){
            val soundList = context.resources.getIntArray(R.array.all_animals)
            val allAnimal = context.resources.getStringArray(R.array.animals_files)
            val allAnimalName = context.resources.getStringArray(R.array.animals)

            val detailsList = arrayListOf<DetailsSound>()
            for (i in soundList.indices){
                if(soundList[i] == 1){

                    val fileName = allAnimal[i]

                    val soundResId = context.resources.getIdentifier(fileName, "raw", context.packageName)
                    val imageResId = "file:///android_asset/image/$fileName.jpg"

                    if (soundResId != 0) {
                        val detailsSound = DetailsSound(allAnimalName[i], soundResId, imageResId)
                        detailsSound.isFavourite = isFavouriteUseCase.execute(
                            CheckIsFavouriteUseCase.Param(soundResId))
                        detailsList.add(detailsSound)
                    }
                }
            }
            detailsList.shuffle()
            detailsListLive.postValue(detailsList)
    }
    }

    fun reCheckStateFavourite() {
        viewModelScope.launch(Dispatchers.IO) {
            if(detailsListLive.value != null){
                val soundList = detailsListLive.value
                soundList?.let {
                    for (detailsSound in soundList){
                        detailsSound.isFavourite = isFavouriteUseCase.execute(
                            CheckIsFavouriteUseCase.Param(detailsSound.soundRes))
                    }
                    detailsListLive.postValue(it)
                }
            }
        }
    }
}