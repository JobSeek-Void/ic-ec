package team.jsv.icec.ui.main.mosaic.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseViewModel
import team.jsv.icec.util.Extras.ResultImageKey
import team.jsv.util_kotlin.MutableDebounceFlow
import team.jsv.util_kotlin.debounceAction

class MosaicResultViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel() {

    private val _image = MutableStateFlow(savedStateHandle.get<String>(ResultImageKey) ?: "")
    val image: StateFlow<String> = _image.asStateFlow()

    private val _mosaicResultEvent = MutableSharedFlow<MosaicResultEvent>()
    val mosaicResultEvent = _mosaicResultEvent.asSharedFlow()

    private val _debounceShareEvent = MutableDebounceFlow<String> {
        debounceAction(
            coroutineScope = viewModelScope,
            timeoutMillis = 200
        ) { mosaicImage ->
            _mosaicResultEvent.emit(MosaicResultEvent.SendMosaicImage(mosaicImage = mosaicImage))
        }
    }

    fun handleMosaicResultEvent(event: MosaicResultEvent) {
        viewModelScope.launch {
            when (event) {
                MosaicResultEvent.Share -> {
                    _debounceShareEvent.emit(_image.value)
                }

                MosaicResultEvent.FinishActivity -> {
                    _mosaicResultEvent.emit(MosaicResultEvent.FinishActivity)
                }

                else -> {}
            }
        }
    }

}
