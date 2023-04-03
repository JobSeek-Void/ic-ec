package team.jsv.data.dto.response

import com.google.gson.annotations.SerializedName

data class BlurResponse (
    @SerializedName("code") val code: Int,
    @SerializedName("blurImage") val blurImage: String
)