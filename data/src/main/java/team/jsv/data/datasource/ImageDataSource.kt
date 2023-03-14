package team.jsv.data.datasource

import team.jsv.data.api.ICECApi
import team.jsv.data.dto.response.FaceResponse
import team.jsv.data.mapper.toMultipartImage
import team.jsv.util_kotlin.IcecNetworkException
import java.io.File
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val icecApi: ICECApi
) {
    suspend fun getFaceList(
        currentTime: String,
        image: File
    ): FaceResponse {
        try {
            return icecApi.getFaceList(
                currentTime = currentTime,
                image = image.toMultipartImage()
            )
        } catch (e: Throwable) {
            throw IcecNetworkException(
                code = "400",
                message = "에러 발생",
            )
        }
    }
}