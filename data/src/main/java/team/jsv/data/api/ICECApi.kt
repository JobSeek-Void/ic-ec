package team.jsv.data.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import team.jsv.data.dto.request.MosaicRequestBody
import team.jsv.data.dto.response.BlurResponse
import team.jsv.data.dto.response.FaceResponse
import team.jsv.data.dto.response.MosaicResponse

interface ICECApi {

    @Multipart
    @POST("/faceList")
    suspend fun getDetectedFace(
        @Part("randomSeed") randomSeed: String,
        @Part("threshold") threshold: Float,
        @Part image: MultipartBody.Part,
    ): FaceResponse

    @POST("/mosaic")
    suspend fun getMosaicImage(
        @Body mosaicRequestBody: MosaicRequestBody,
    ): MosaicResponse

    @POST("/blur")
    suspend fun getBlurImage(
        @Body mosaicRequestBody: MosaicRequestBody,
    ): BlurResponse

}
