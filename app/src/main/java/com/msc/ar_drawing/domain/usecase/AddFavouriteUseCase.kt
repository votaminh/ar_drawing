package com.msc.ar_drawing.domain.usecase

import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.data.repositories.FavouriteSoundRepository
import javax.inject.Inject

class AddFavouriteUseCase @Inject constructor(val favouriteSoundRepository: FavouriteSoundRepository) : UseCase<AddFavouriteUseCase.Param, Long>() {
    class Param(val detailsSound: DetailsSound) : UseCase.Param()

    override suspend fun execute(param: Param): Long {
        return favouriteSoundRepository.insert(param.detailsSound)
    }
}