package team.jsv.icec.ui.main.mosaic.result

sealed class MosaicResultEvent {

    object Share : MosaicResultEvent()

    object FinishActivity : MosaicResultEvent()

    class SendMosaicImage(val mosaicImage: String) : MosaicResultEvent()

}
