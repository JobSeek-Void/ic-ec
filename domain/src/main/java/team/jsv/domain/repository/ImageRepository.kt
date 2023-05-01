package team.jsv.domain.repository

import team.jsv.domain.model.Face
import team.jsv.domain.model.MosaicImage
import java.io.File

interface ImageRepository {

    suspend fun getDetectedFace(
        currentTime: String,
        threshold: Float,
        image: File
    ): Face

    suspend fun getMosaicImage(
        currentTime: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>
    ): MosaicImage

}