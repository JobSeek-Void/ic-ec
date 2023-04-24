package team.jsv.icec.ui.main.mosaic.mosaicFace

import team.jsv.domain.model.MosaicType

internal const val DEFAULT_MOSAIC_STRENGTH = 20f

data class MosaicFaceState(
    val mosaicType: MosaicType = MosaicType.None,
    val pixelSize: Float = DEFAULT_MOSAIC_STRENGTH,
)