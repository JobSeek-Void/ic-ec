package team.jsv.icec.ui.main.mosaic.result

sealed class MosaicResultEvent {

    object OnClickFinish : MosaicResultEvent()

    class OnClickShare(val mosaicImage: String) : MosaicResultEvent()

}
