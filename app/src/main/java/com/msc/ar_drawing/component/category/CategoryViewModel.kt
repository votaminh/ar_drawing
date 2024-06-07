package com.msc.ar_drawing.component.category

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msc.ar_drawing.R
import com.msc.ar_drawing.domain.layer.CategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(@ApplicationContext val  context: Context) : ViewModel() {

    val categoryListLive = MutableLiveData<List<CategoryModel>>()

    fun getAllCategory(){
        val categoryList = arrayListOf<CategoryModel>()
        categoryList.add(CategoryModel(R.string.txt_all_animal, "file:///android_asset/image/animals_antylopa.jpg", R.array.all_animals))
        categoryList.add(CategoryModel(R.string.txt_bird, "file:///android_asset/image/animals_pingwin.jpg", R.array.birds))
        categoryList.add(CategoryModel(R.string.txt_farm_animals, "file:///android_asset/image/animals_koala.jpg", R.array.farm_animals))
        categoryList.add(CategoryModel(R.string.txt_insects, "file:///android_asset/image/animals_kogut.jpg", R.array.insects))
        categoryList.add(CategoryModel(R.string.txt_land, "file:///android_asset/image/animals_goryl.jpg", R.array.land))
        categoryList.add(CategoryModel(R.string.txt_mammals, "file:///android_asset/image/animals_foka.jpg", R.array.mammals))
        categoryList.add(CategoryModel(R.string.txt_pet_animals, "file:///android_asset/image/animals_pszczola.jpg", R.array.pet_animals))
        categoryList.add(CategoryModel(R.string.txt_reptiles_and_amphibians, "file:///android_asset/image/animals_zebra.jpg", R.array.reptiles_and_amphibians))
        categoryList.add(CategoryModel(R.string.txt_switching_array, "file:///android_asset/image/animals_sowa.jpg", R.array.switching_array))
        categoryList.add(CategoryModel(R.string.txt_water, "file:///android_asset/image/animals_kangur.jpg", R.array.water))
        categoryList.add(CategoryModel(R.string.txt_wild_animals, "file:///android_asset/image/animals_zyrafa.jpg", R.array.wild_animals))

        categoryListLive.postValue(categoryList)
    }
}