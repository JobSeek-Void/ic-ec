package team.jsv.data.dto.response

import com.google.gson.annotations.SerializedName

data class FaceResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("originalImage") val originalImage: String,
    @SerializedName("faceList") val faceList: List<FaceInfo>
)

data class FaceInfo(
    @SerializedName("url") val url: String,
    @SerializedName("coordinates") val coordinates: List<Int>
)