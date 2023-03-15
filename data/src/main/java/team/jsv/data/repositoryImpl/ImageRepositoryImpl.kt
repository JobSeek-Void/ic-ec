package team.jsv.data.repositoryImpl

import team.jsv.data.datasource.ImageDataSource
import team.jsv.data.mapper.toDomain
import team.jsv.domain.model.Face
import team.jsv.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDataSource: ImageDataSource
) : ImageRepository {

    override suspend fun getDetectedFace(
        currentTime: String,
        image: File
    ): Face {
        return imageDataSource.getDetectedFace(
            currentTime = currentTime,
            image = image
        ).toDomain()
    }

}