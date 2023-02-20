package team.jsv.data.dto.response

import com.google.gson.annotations.SerializedName

data class FaceResponse(
    @SerializedName("faceList") val faceList: List<String>
)
