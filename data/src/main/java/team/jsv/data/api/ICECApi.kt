package team.jsv.data.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import team.jsv.data.dto.response.FaceResponse

interface ICECApi {

    @Multipart
    @POST("/faceList")
    suspend fun getFaceList(
        @Part("currentTime") currentTime: String,
        @Part image: MultipartBody.Part,
    ): FaceResponse

}
