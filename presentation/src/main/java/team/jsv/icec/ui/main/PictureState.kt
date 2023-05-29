package team.jsv.icec.ui.main

typealias file = java.io.File

sealed class PictureState {
    class File(value: file) : PictureState()
    class Url(value: String) : PictureState()
}