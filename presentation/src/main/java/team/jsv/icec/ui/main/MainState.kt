package team.jsv.icec.ui.main

import java.io.File

data class MainState(
    val pictureState: PictureState = PictureState(),
    val screenStep: ScreenStep = ScreenStep.default(),
)

enum class ScreenStep {
    SelectFace,
    MosaicFace;

    companion object {
        fun default() = SelectFace
    }
}


data class PictureState(
    val viewType: ViewType = ViewType.Original,
    val originalImage: File = File(""),
    val mosaicImage: String = "",
) {
    enum class ViewType {
        Original,
        Mosaic,
    }
}