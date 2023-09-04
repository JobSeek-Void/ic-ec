package team.jsv.data.dto.request

import com.google.gson.annotations.SerializedName

data class MosaicRequestBody(
    @SerializedName("randomSeed") val randomSeed: String,
    @SerializedName("pixelSize") val pixelSize: Int,
    @SerializedName("originalImage") val originalImage: String,
    @SerializedName("coordinates") val coordinates: List<List<Int>>
)
