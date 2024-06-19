package com.msc.ar_drawing.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetListImageUseCase @Inject constructor(@ApplicationContext val context : Context) : UseCase<GetListImageUseCase.Param, List<String>>() {

    class Param : UseCase.Param(){}

    override suspend fun execute(param: Param): List<String> {
        val images = arrayListOf<String>()
        val listImagePath = context.assets.list("image")
        listImagePath?.forEach {
            images.add("image/$it")
        }

        return images
    }
}