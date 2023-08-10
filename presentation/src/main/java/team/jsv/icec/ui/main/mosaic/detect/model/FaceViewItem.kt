package team.jsv.icec.ui.main.mosaic.detect.model

import team.jsv.domain.model.Face

data class FaceViewItem(
    val originalImage: String = "",
    val faceList: List<DetectFaceInfoViewItem> = emptyList()
)

data class DetectFaceInfoViewItem(
    val url: String,
    val coordinates: List<Int>,
    val selected: Boolean = false
)

fun Face.toFaceViewItem(): FaceViewItem {
    return FaceViewItem(
        originalImage = originalImage,
        faceList = faceList.map { detectFaceInfo ->
            DetectFaceInfoViewItem(
                url = detectFaceInfo.url,
                coordinates = detectFaceInfo.coordinates
            )
        }
    )
}