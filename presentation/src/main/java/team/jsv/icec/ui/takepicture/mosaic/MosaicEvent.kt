package team.jsv.icec.ui.main.mosaic

sealed class MosaicEvent {
    class SendToast(val message: String) : MosaicEvent()
}
