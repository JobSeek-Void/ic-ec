package team.jsv.icec.ui.main.mosaic.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseViewModel
import team.jsv.icec.util.Extras.ResultImageKey

class MosaicResultViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel() {

    private val _image = MutableStateFlow(savedStateHandle.get<String>(ResultImageKey) ?: "")
    val image: StateFlow<String> get() = _image

    private val _mosaicResultEvent = MutableSharedFlow<MosaicResultEvent>()
    val mosaicResultEvent = _mosaicResultEvent.asSharedFlow()

    fun handleMosaicResultEvent(event: MosaicResultEvent.Event) {
        viewModelScope.launch {
            when (event) {
                MosaicResultEvent.Event.Share -> {
                    _mosaicResultEvent.emit(MosaicResultEvent.SendMosaicImage(_image.value))
                }

                MosaicResultEvent.Event.ActivityFinish -> {
                    _mosaicResultEvent.emit(MosaicResultEvent.FinishActivity)
                }
            }
        }
    }

}
