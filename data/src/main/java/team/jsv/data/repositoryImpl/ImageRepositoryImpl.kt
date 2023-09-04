package team.jsv.data.repositoryImpl

import team.jsv.data.datasource.ImageDataSource
import team.jsv.data.mapper.toDomain
import team.jsv.domain.model.Face
import team.jsv.domain.model.MosaicImage
import team.jsv.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDataSource: ImageDataSource,
) : ImageRepository {

    override suspend fun getDetectedFace(
        randomSeed: String,
        threshold: Float,
        image: File
    ): Face {
        return imageDataSource.getDetectedFace(
            randomSeed = randomSeed,
            threshold = threshold,
            image = image
        ).toDomain()
    }

    override suspend fun getMosaicImage(
        randomSeed: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
    ): MosaicImage {
        return imageDataSource.getMosaicImage(
            randomSeed = randomSeed,
            pixelSize = pixelSize,
            originalImage = originalImage,
            coordinates = coordinates
        ).toDomain()
    }

    override suspend fun getBlurImage(
        randomSeed: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
    ): MosaicImage {
        return imageDataSource.getBlurImage(
            randomSeed = randomSeed,
            pixelSize = pixelSize,
            originalImage = originalImage,
            coordinates = coordinates
        ).toDomain()
    }

}