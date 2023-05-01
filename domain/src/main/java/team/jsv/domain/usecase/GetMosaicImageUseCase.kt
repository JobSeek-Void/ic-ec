package team.jsv.domain.usecase

import team.jsv.domain.model.MosaicType
import team.jsv.domain.repository.ImageRepository
import javax.inject.Inject

class GetMosaicImageUseCase @Inject constructor(
    private val repository: ImageRepository,
) {
    suspend operator fun invoke(
        currentTime: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
        mosaicType: MosaicType,
    ) = runCatching {
        when (mosaicType) {
            MosaicType.Mosaic -> repository.getMosaicImage(
                currentTime = currentTime,
                pixelSize = pixelSize,
                originalImage = originalImage,
                coordinates = coordinates
            )
            MosaicType.Blur -> repository.getBlurImage(
                currentTime = currentTime,
                pixelSize = pixelSize,
                originalImage = originalImage,
                coordinates = coordinates
            )
            MosaicType.None -> throw IllegalArgumentException("MosaicType.None is not allowed")
        }
    }
}