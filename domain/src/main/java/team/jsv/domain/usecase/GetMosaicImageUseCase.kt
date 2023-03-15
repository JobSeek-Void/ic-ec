package team.jsv.domain.usecase

import team.jsv.domain.repository.ImageRepository
import javax.inject.Inject

class GetMosaicImageUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(
        currentTime: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>
    ) = runCatching {
        repository.getMosaicImage(
            currentTime = currentTime,
            pixelSize = pixelSize,
            originalImage = originalImage,
            coordinates = coordinates
        )
    }
}