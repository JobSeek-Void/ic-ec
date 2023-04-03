package team.jsv.domain.usecase

import team.jsv.domain.repository.ImageRepository
import javax.inject.Inject

class GetBlurImageUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(
        currentTime: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<Int>
    ) = runCatching {
        repository.getBlurImg(
            currentTime = currentTime,
            pixelSize = pixelSize,
            originalImage = originalImage,
            coordinates = coordinates
        )
    }
}