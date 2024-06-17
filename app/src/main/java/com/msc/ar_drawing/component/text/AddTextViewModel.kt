package com.msc.ar_drawing.component.text

import android.content.Context
import android.graphics.Color
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
    val colorsLive = MutableLiveData<List<Int>>()

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

    fun getColors() {
        CoroutineScope(Dispatchers.IO).launch {
            val colors = arrayListOf<Int>()
            colors.add(-1)
            colors.add(Color.parseColor("#80e65c"))
            colors.add(Color.parseColor("#a3a3a3"))
            colors.add(Color.parseColor("#af7961"))
            colors.add(Color.parseColor("#d9d9d9"))
            colors.add(Color.parseColor("#0b0b0b"))
            colors.add(Color.parseColor("#50e2bf"))
            colors.add(Color.parseColor("#f047ac"))
            colors.add(Color.parseColor("#ba6bfc"))
            colorsLive.postValue(colors)
        }
    }
}