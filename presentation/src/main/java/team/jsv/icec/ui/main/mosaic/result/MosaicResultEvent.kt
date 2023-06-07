package team.jsv.icec.ui.main.mosaic.result

sealed class MosaicResultEvent {

    enum class Event {
        Share,
        ActivityFinish;
    }

    object FinishActivity : MosaicResultEvent()

    class SendMosaicImage(val mosaicImage: String) : MosaicResultEvent()

}
