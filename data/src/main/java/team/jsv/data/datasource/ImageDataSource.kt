package team.jsv.data.datasource

import team.jsv.data.api.ICECApi
import team.jsv.data.dto.response.FaceResponse
import team.jsv.data.mapper.toMultipartImage
import java.io.File
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val icecApi: ICECApi
) {
    suspend fun getFaceList(
        currentTime: String,
        image: File
    ) : FaceResponse {
        return icecApi.getFaceList(
            currentTime = currentTime,
            image = image.toMultipartImage()
        )
    }
}