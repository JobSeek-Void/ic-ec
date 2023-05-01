package team.jsv.domain.model

data class Face(
    val originalImage: String,
    val faceList: List<DetectFaceInfo>
)

data class DetectFaceInfo(
    val url: String,
    val coordinates: List<Int>
)
