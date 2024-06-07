package com.msc.ar_drawing.domain.usecase

import com.msc.ar_drawing.R
import com.msc.ar_drawing.domain.layer.LanguageModel
import javax.inject.Inject

class GetListLanguageUseCase @Inject constructor() :
    UseCase<GetListLanguageUseCase.Param, List<LanguageModel>>() {

    open class Param() : UseCase.Param()

    override suspend fun execute(param: Param): List<LanguageModel> = listOf(
        LanguageModel("vi", R.drawable.ic_vietnamese, R.string.vietnamese),
        LanguageModel("hi", R.drawable.ic_hindi, R.string.hindi),
        LanguageModel("es", R.drawable.ic_spanish, R.string.spanish),
        LanguageModel("en", R.drawable.ic_english, R.string.english),
        LanguageModel("fr", R.drawable.ic_france, R.string.french),
        LanguageModel("pt", R.drawable.ic_portugal, R.string.portuguese)
    )
}