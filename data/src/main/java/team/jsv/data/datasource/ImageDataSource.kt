package team.jsv.data.datasource

import team.jsv.data.api.ICECApi
import team.jsv.data.dto.request.MosaicRequestBody
import team.jsv.data.dto.response.FaceResponse
import team.jsv.data.dto.response.MosaicResponse
import team.jsv.data.mapper.toMultipartImage
import team.jsv.util_kotlin.IcecNetworkException
import java.io.File
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val icecApi: ICECApi,
) {

    suspend fun getDetectedFace(
        currentTime: String,
        threshold: Float,
        image: File
    ): FaceResponse {
        try {
            return icecApi.getDetectedFace(
                currentTime = currentTime,
                threshold =  threshold,
                image = image.toMultipartImage()
            )
        } catch (e: Throwable) {
            throw IcecNetworkException(
                code = "400",
                message = "에러 발생",
            )
        }
    }

    suspend fun getMosaicImage(
        currentTime: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
    ): MosaicResponse {
        try {
            return icecApi.getMosaicImage(
                mosaicRequestBody = MosaicRequestBody(
                    currentTime = currentTime,
                    pixelSize = pixelSize,
                    originalImage = originalImage,
                    coordinates = coordinates
                )
            )
        } catch (e: Throwable) {
            throw IcecNetworkException(
                code = "400",
                message = "에러 발생",
            )
        }
    }

    suspend fun getBlurImage(
        currentTime: String,
        pixelSize: Int,
        originalImage: String,
        coordinates: List<List<Int>>,
    ): MosaicResponse {
        try {
            return icecApi.getBlurImage(
                mosaicRequestBody = MosaicRequestBody(
                    currentTime = currentTime,
                    pixelSize = pixelSize,
                    originalImage = originalImage,
                    coordinates = coordinates
                )
            )
        } catch (e: Throwable) {
            throw IcecNetworkException(
                code = "400",
                message = "에러 발생",
            )
        }
    }

}