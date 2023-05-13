package team.jsv.icec.ui.main.mosaic.detect

import team.jsv.icec.ui.main.mosaic.detect.model.FaceViewItem

internal const val DEFAULT_DETECT_STRENGTH = 0.9f

data class DetectFaceState(
    val faceList: FaceViewItem = FaceViewItem(),
    val value: Float = DEFAULT_DETECT_STRENGTH
)
