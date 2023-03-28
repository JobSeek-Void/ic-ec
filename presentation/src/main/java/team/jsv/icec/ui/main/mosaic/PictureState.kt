package team.jsv.icec.ui.main.mosaic

sealed class PictureState {
    class File(value: java.io.File) : PictureState()
    class Url(value: String) : PictureState()
}
