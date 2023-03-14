package team.jsv.domain.model

data class Face(
    val code: Int,
    val originalImage: String,
    val faceList: List<DetectFaceInfo>
)

data class DetectFaceInfo(
    val url: String,
    val coordinates: List<Int>
)
