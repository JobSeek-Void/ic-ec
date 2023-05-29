package team.jsv.icec.ui.main

sealed class MosaicEvent {
    class SendToast(val message: String) : MosaicEvent()
}