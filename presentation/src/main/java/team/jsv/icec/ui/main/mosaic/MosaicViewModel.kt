package team.jsv.icec.ui.main.mosaic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import team.jsv.domain.model.MosaicType
import team.jsv.domain.usecase.GetDetectedFaceUseCase
import team.jsv.domain.usecase.GetMosaicImageUseCase
import team.jsv.icec.base.BaseViewModel
import team.jsv.icec.base.Event
import team.jsv.icec.base.updateState
import team.jsv.icec.ui.main.mosaic.detect.DetectFaceState
import team.jsv.icec.ui.main.mosaic.detect.model.FaceViewItem
import team.jsv.icec.ui.main.mosaic.detect.model.toFaceViewItem
import team.jsv.icec.ui.main.mosaic.mosaicFace.DEFAULT_MOSAIC_STRENGTH
import team.jsv.icec.ui.main.mosaic.mosaicFace.MosaicFaceState
import team.jsv.icec.util.toThreshold
import team.jsv.util_kotlin.IcecNetworkException
import team.jsv.util_kotlin.MutableDebounceFlow
import team.jsv.util_kotlin.copy
import team.jsv.util_kotlin.debounceAction
import team.jsv.util_kotlin.toFormatString
import java.io.File
import java.util.Date
import javax.inject.Inject

enum class ScreenStep {
    SelectMosaicEdit,
    SelectFace,
    MosaicFace;
}

@HiltViewModel
internal class MosaicViewModel @Inject constructor(
    private val getDetectedFaceUseCase: GetDetectedFaceUseCase,
    private val getMosaicImageUseCase: GetMosaicImageUseCase,
) : BaseViewModel() {

    companion object {
        private const val debounceDelay: Long = 200L
        private const val DEFAULT_CURRENT_TIME_FORMAT = "yyyy-MM-dd-HHmmss"
    }

    private val currentTime: String = Date().toFormatString(DEFAULT_CURRENT_TIME_FORMAT)

    private val _originalImage = MutableLiveData<File>()
    val originalImage: LiveData<File> get() = _originalImage

    private val _mosaicImage = MutableLiveData<Event<String>>()
    val mosaicImage: LiveData<Event<String>> get() = _mosaicImage

    private val _screenStep =
        MutableLiveData<ScreenStep>().apply { postValue(ScreenStep.SelectMosaicEdit) }
    val screenStep: LiveData<ScreenStep> get() = _screenStep

    private val _mosaicEvent = MutableLiveData<Event<MosaicEvent>>()
    val mosaicEvent: LiveData<Event<MosaicEvent>> get() = _mosaicEvent

    private val _state = MutableLiveData<PictureState>()
    val state: LiveData<PictureState> get() = _state

    private val _detectedFaceIndexes = MutableStateFlow<List<Int>>(emptyList())
    val detectedFaceIndexes: StateFlow<List<Int>> = _detectedFaceIndexes.asStateFlow()

    private val _detectFaceState = MutableStateFlow(DetectFaceState())
    val detectFaceState = _detectFaceState.asStateFlow()

    private val _mosaicFaceState = MutableStateFlow(MosaicFaceState())
    val mosaicFaceState = _mosaicFaceState.asStateFlow()

    private val mosaicDebounce = MutableDebounceFlow<Unit> {
        debounceAction(
            coroutineScope = viewModelScope,
            timeoutMillis = debounceDelay,
        ) {
            getMosaicImage()
        }
    }

    fun setScreen(screenStep: ScreenStep) = _screenStep.postValue(screenStep)

    fun setImage(data: File) {
        _originalImage.value = data
        _state.postValue(PictureState.File(data))
    }

    fun setPixelSize(value: Float) {
        _mosaicFaceState.updateState {
            copy(pixelSize = value)
        }
    }

    fun setImageAboutScreenStep() {
        when (_screenStep.value) {
            ScreenStep.MosaicFace -> {
                _state.postValue(PictureState.File(_originalImage.value ?: File("")))
            }
            else -> {}
        }
    }

    fun setOnClickAllSelectButton() {
        viewModelScope.launch {
            _detectedFaceIndexes.value.copy {
                if (size == _detectFaceState.value.faceViewItem.faceList.size || size >= 1) {
                    clear()
                } else {
                    clear()
                    addAll(_detectFaceState.value.faceViewItem.faceList.indices)
                }
            }.also { _detectedFaceIndexes.value = it }
        }
    }

    fun setOnClickItem(index: Int) {
        viewModelScope.launch {
            _detectedFaceIndexes.value.copy {
                if (contains(index)) {
                    remove(index)
                } else {
                    add(index)
                }
            }.also { _detectedFaceIndexes.value = it }
        }
    }

    fun setOnClearDetectedFaceIndex() {
        _detectedFaceIndexes.value = emptyList()
    }

    fun setFaceViewItem(faceViewItem: FaceViewItem) {
        _detectFaceState.updateState {
            copy(faceViewItem = faceViewItem)
        }
    }

    fun setDetectStrength(value: Float) {
        _detectFaceState.updateState {
            copy(detectStrength = value)
        }
    }

    fun setDetectFaceLoading(isLoading: Boolean) {
        _detectFaceState.updateState {
            copy(isLoading = isLoading)
        }
    }

    fun getFaceList() {
        viewModelScope.launch {
            setDetectFaceLoading(true)
            with(_detectFaceState.value) {
                getDetectedFaceUseCase(
                    currentTime = currentTime,
                    threshold = detectStrength.toThreshold,
                    image = _originalImage.value ?: File("")
                ).onSuccess { data ->
                    data.toFaceViewItem().also { faceViewItem ->
                        setFaceViewItem(faceViewItem)
                    }
                    setOnClearDetectedFaceIndex()
                }.onFailure {
                    handleException(it)
                }.also { setDetectFaceLoading(false) }
            }
        }
    }

    fun emitMosaicEvent() {
        viewModelScope.launch {
            mosaicDebounce.emit(Unit)
        }
    }

    fun getMosaicImage() {
        viewModelScope.launch {
            val originalImage = _detectFaceState.value.faceViewItem.originalImage
            val indexes = _detectedFaceIndexes.value
            val coordinates = indexes.map { _detectFaceState.value.faceViewItem.faceList[it].coordinates }
                .ifEmpty { listOf(listOf()) }
            with(_mosaicFaceState.value) {
                getMosaicImageUseCase(
                    currentTime = currentTime,
                    pixelSize = pixelSize.toInt(),
                    originalImage = originalImage,
                    coordinates = coordinates,
                    mosaicType = mosaicType
                ).onSuccess {
                    when (screenStep.value) {
                        ScreenStep.MosaicFace -> {
                            _mosaicImage.postValue(Event(it.blurImage))
                            _state.postValue(PictureState.Url(it.blurImage))
                        }
                        else -> {
                            _state.postValue(_originalImage.value?.let { originalImage ->
                                PictureState.File(originalImage)
                            })
                        }
                    }
                }.onFailure {
                    handleException(it)
                }
            }
        }
    }

    fun setMosaicType(mosaicType: MosaicType) {
        _mosaicFaceState.updateState {
            copy(mosaicType = mosaicType)
        }
        emitMosaicEvent()
    }

    fun mosaicFaceRefresh() {
        _mosaicFaceState.updateState {
            copy(
                mosaicType = MosaicType.default(),
                pixelSize = DEFAULT_MOSAIC_STRENGTH,
            )
        }
        emitMosaicEvent()
    }

    private fun handleException(exception: Throwable) {
        if (exception is IcecNetworkException) {
            Log.d("실패", exception.toString())
            _mosaicEvent.postValue(Event(MosaicEvent.SendToast(exception.message)))
        }
    }
}
