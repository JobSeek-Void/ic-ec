package team.jsv.domain.model

enum class MosaicType {
    Mosaic,
    Blur,
    None,
    ;

    companion object {
        fun default() = Mosaic
    }
}