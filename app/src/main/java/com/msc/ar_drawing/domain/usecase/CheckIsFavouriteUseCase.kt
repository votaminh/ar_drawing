package com.msc.ar_drawing.domain.usecase

import com.msc.ar_drawing.data.repositories.FavouriteSoundRepository
import javax.inject.Inject

class CheckIsFavouriteUseCase @Inject constructor(private val favouriteSoundRepository: FavouriteSoundRepository)
    : UseCase<CheckIsFavouriteUseCase.Param, Boolean>() {
    class Param(val soundRes : Int) : UseCase.Param()

    override suspend fun execute(param: Param): Boolean {
        return favouriteSoundRepository.getFavourite(param.soundRes) != null
    }
}