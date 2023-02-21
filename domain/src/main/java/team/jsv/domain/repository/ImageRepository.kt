package team.jsv.domain.repository

import team.jsv.domain.model.Face
import java.io.File

interface ImageRepository {

    suspend fun getFaceList(
        currentTime: String,
        image: File
    ): List<Face>

}