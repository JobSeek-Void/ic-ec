package team.jsv.icec.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
import team.jsv.icec.util.Extras
import team.jsv.icec.util.toThreshold
import team.jsv.util_kotlin.IcecNetworkException
import team.jsv.util_kotlin.copy
import team.jsv.util_kotlin.toFormatString
import java.io.File
import java.util.Date
import javax.inject.Inject

enum class ScreenStep {
    SelectFace,
    MosaicFace;
}

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDetectedFaceUseCase: GetDetectedFaceUseCase,
    private val getMosaicImageUseCase: GetMosaicImageUseCase,
) : BaseViewModel() {

    private val currentTime: String = Date().toFormatString(DEFAULT_CURRENT_TIME_FORMAT)

    private lateinit var _originalImage: File
    var originalImage: File
        get() = _originalImage
        set(value) {
            _originalImage = value
        }

    private lateinit var _mosaicImage: String
    var mosaicImage: String
        get() = _mosaicImage
        set(value) {
            _mosaicImage = value
        }

    private val _state = MutableLiveData<PictureState>()
    val state: LiveData<PictureState> get() = _state

    private val _screenStep = MutableLiveData<ScreenStep>()
    val screenStep: LiveData<ScreenStep> get() = _screenStep

    private val _mosaicEvent = MutableLiveData<Event<MosaicEvent>>()
    val mosaicEvent: LiveData<Event<MosaicEvent>> get() = _mosaicEvent

    private val _detectedFaceIndexes = MutableStateFlow<List<Int>>(emptyList())
    val detectedFaceIndexes: StateFlow<List<Int>> = _detectedFaceIndexes.asStateFlow()

    private val _detectFaceState = MutableStateFlow(DetectFaceState())
    val detectFaceState = _detectFaceState.asStateFlow()

    private val _mosaicFaceState = MutableStateFlow(MosaicFaceState())
    val mosaicFaceState = _mosaicFaceState.asStateFlow()

    init {
        initImage()
        getFaceList()
    }

    private fun initImage() {
        val image = File(savedStateHandle.get<String>(Extras.ImagePath) ?: "")
        _originalImage = image
        _state.value = PictureState.File(image)
        _screenStep.value = ScreenStep.SelectFace
    }

    fun nextScreenStep() {
        val currentScreenStep = _screenStep.value ?: return

         when(currentScreenStep) {
            ScreenStep.SelectFace -> {
                _screenStep.value = ScreenStep.MosaicFace
            }
            ScreenStep.MosaicFace -> {
                _screenStep.value = ScreenStep.SelectFace
            }
        }
    }

    fun setScreen(screenStep: ScreenStep) {
        _screenStep.postValue(screenStep)
    }

    fun setOriginalImage() {
        _state.postValue(PictureState.File(_originalImage))
    }

    fun setMosaicImage() {
        if (::_mosaicImage.isInitialized) {
            _state.postValue(PictureState.Url(_mosaicImage))
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
        }.let { getFaceList() }
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
                    image = _originalImage
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

    fun getMosaicImage() {
        viewModelScope.launch {
            _mosaicFaceState.updateState { copy(isLoading = true) }
            val originalImage = _detectFaceState.value.faceViewItem.originalImage
            val indexes = _detectedFaceIndexes.value
            val coordinates =
                indexes.map { _detectFaceState.value.faceViewItem.faceList[it].coordinates }
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
                            _mosaicImage = it.blurImage
                            _state.postValue(PictureState.Url(it.blurImage))
                        }
                        else -> {
                            _state.postValue(
                                PictureState.File(_originalImage))
                        }
                    }
                }.onFailure {
                    handleException(it)
                }.also {
                    _mosaicFaceState.updateState { copy(isLoading = false) }
                }
            }
        }
    }

    fun setMosaicType(mosaicType: MosaicType) {
        _mosaicFaceState.updateState {
            copy(mosaicType = mosaicType)
        }
        getMosaicImage()
    }

    fun setPixelSize(value: Float) {
        _mosaicFaceState.updateState {
            copy(pixelSize = value)
        }
    }

    fun mosaicFaceRefresh() {
        _mosaicFaceState.updateState {
            copy(
                mosaicType = MosaicType.default(),
                pixelSize = DEFAULT_MOSAIC_STRENGTH,
            )
        }
        getMosaicImage()
    }

    private fun handleException(exception: Throwable) {
        if (exception is IcecNetworkException) {
            Log.d("실패", exception.toString())
            _mosaicEvent.postValue(Event(MosaicEvent.SendToast(exception.message)))
        }
    }

    companion object {
        private const val DEFAULT_CURRENT_TIME_FORMAT = "yyyy-MM-dd-HHmmss"
    }

}
