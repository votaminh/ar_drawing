package com.msc.ar_drawing.domain.usecase

import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.data.repositories.FavouriteSoundRepository
import javax.inject.Inject

class UnFavouriteUseCase @Inject constructor(val favouriteSoundRepository: FavouriteSoundRepository) : UseCase<UnFavouriteUseCase.Param, Int>() {
    class Param(val detailsSound: DetailsSound) : UseCase.Param()

    override suspend fun execute(param: Param): Int {
        return favouriteSoundRepository.remove(param.detailsSound)
    }
}