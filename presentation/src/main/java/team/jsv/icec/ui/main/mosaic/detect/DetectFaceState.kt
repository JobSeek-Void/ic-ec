package team.jsv.icec.ui.main.mosaic.detect

import team.jsv.icec.base.LoadingState
import team.jsv.icec.ui.main.mosaic.detect.model.FaceViewItem

private const val DEFAULT_DETECT_STRENGTH = 0.9f

data class DetectFaceState(
    val faceViewItem: FaceViewItem = FaceViewItem(),
    val detectStrength: Float = DEFAULT_DETECT_STRENGTH,
    override val isLoading: Boolean = false,
) : LoadingState()
