package com.msc.ar_drawing.component.details

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msc.ar_drawing.R
import com.msc.ar_drawing.domain.usecase.AddFavouriteUseCase
import com.msc.ar_drawing.domain.usecase.CheckIsFavouriteUseCase
import com.msc.ar_drawing.domain.usecase.UnFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    @ApplicationContext val context : Context,
    val addFavouriteUseCase: AddFavouriteUseCase,
    val unFavouriteUseCase: UnFavouriteUseCase,
    val isFavouriteUseCase: CheckIsFavouriteUseCase
) : ViewModel() {

    val detailsListLive = MutableLiveData<List<DetailsSound>>()

    val playDoneLive = MutableLiveData<DetailsSound>()

    private var mediaPlayer : MediaPlayer? = null

    @SuppressLint("DiscouragedApi")
    fun getListDetailsFromRes(res : Int){
        viewModelScope.launch(Dispatchers.IO){
            val soundList = context.resources.getIntArray(res)
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
                        detailsSound.isFavourite = isFavouriteUseCase.execute(CheckIsFavouriteUseCase.Param(soundResId))
                        detailsList.add(detailsSound)
                    }
                }
            }

            detailsListLive.postValue(detailsList)
        }
    }

    fun favourite(it: DetailsSound) {
        viewModelScope.launch(Dispatchers.IO) {
            addFavouriteUseCase.execute(AddFavouriteUseCase.Param(it))
        }
    }

    fun unfavoured(it: DetailsSound) {
        viewModelScope.launch(Dispatchers.IO) {
            unFavouriteUseCase.execute(UnFavouriteUseCase.Param(it))
        }
    }

    fun playAudio(it: DetailsSound) {
        mediaPlayer?.stop()
        mediaPlayer = MediaPlayer.create(context, it.soundRes)
        mediaPlayer?.setOnCompletionListener {mediaPlayer ->
            it.isPlaying = false
            playDoneLive.postValue(it)
        }
        mediaPlayer?.start()
    }

    fun stopMedia() {
        mediaPlayer?.setOnCompletionListener {mediaPlayer ->
        }
        mediaPlayer?.stop()
    }
}