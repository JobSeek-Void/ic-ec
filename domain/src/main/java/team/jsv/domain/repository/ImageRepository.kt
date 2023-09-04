package team.jsv.domain.repository

import team.jsv.domain.model.Face
import team.jsv.domain.model.MosaicImage
import java.io.File

interface ImageRepository {

    suspend fun getDetectedFace(
        randomSeed: String,
        threshold: Float,
        image: File
    ): Face

    suspend fun getMosaicImage(
        randomSeed: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
    ): MosaicImage

    suspend fun getBlurImage(
        randomSeed: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
    ): MosaicImage
}