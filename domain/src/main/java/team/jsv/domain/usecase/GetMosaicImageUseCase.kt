package team.jsv.domain.usecase

import team.jsv.domain.model.MosaicType
import team.jsv.domain.repository.ImageRepository
import java.util.UUID
import javax.inject.Inject

class GetMosaicImageUseCase @Inject constructor(
    private val repository: ImageRepository,
) {
    suspend operator fun invoke(
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
        mosaicType: MosaicType,
    ) = runCatching {
        when (mosaicType) {
            MosaicType.Mosaic -> repository.getMosaicImage(
                randomSeed = UUID.randomUUID().toString(),
                pixelSize = pixelSize,
                originalImage = originalImage,
                coordinates = coordinates
            )
            MosaicType.Blur -> repository.getBlurImage(
                randomSeed = UUID.randomUUID().toString(),
                pixelSize = pixelSize / 10,
                originalImage = originalImage,
                coordinates = coordinates
            )
            MosaicType.None -> throw IllegalArgumentException("MosaicType.None is not allowed")
        }
    }
}