package com.msc.ar_drawing.domain.usecase

import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.data.repositories.FavouriteSoundRepository
import javax.inject.Inject

class GetListFavouriteUseCase @Inject constructor(private val favouriteSoundRepository: FavouriteSoundRepository)
    : UseCase<GetListFavouriteUseCase.Param, List<DetailsSound>>() {
    class Param : UseCase.Param()

    override suspend fun execute(param: Param): List<DetailsSound> {
        return favouriteSoundRepository.getList()
    }
}