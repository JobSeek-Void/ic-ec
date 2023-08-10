package team.jsv.icec.ui.main

sealed class MainEvent {
    object Finish : MainEvent()

    object NavigateToMosaicFace: MainEvent()

    object NavigateToMosaicResult: MainEvent()

    class SendToast(val message: String) : MainEvent()
}