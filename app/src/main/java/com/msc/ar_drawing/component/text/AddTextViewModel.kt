package com.msc.ar_drawing.component.text

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTextViewModel @Inject constructor(@ApplicationContext private val context : Context) : ViewModel() {

    val fontsLive = MutableLiveData<List<Typeface>>()

    fun getFonts(){
        CoroutineScope(Dispatchers.IO).launch {
            val list = context.assets.list("font")
            val fontList = arrayListOf<Typeface>()
            list?.forEach {
                fontList.add(Typeface.createFromAsset(context.assets, "font/$it"))
            }
            fontsLive.postValue(fontList)
        }
    }
}