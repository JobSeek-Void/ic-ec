package team.jsv.icec.ui.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import team.jsv.domain.model.MosaicType
import team.jsv.domain.usecase.GetDetectedFaceUseCase
import team.jsv.domain.usecase.GetMosaicImageUseCase
import team.jsv.icec.base.BaseViewModel
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

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDetectedFaceUseCase: GetDetectedFaceUseCase,
    private val getMosaicImageUseCase: GetMosaicImageUseCase,
) : BaseViewModel() {

    private val currentTime: String = Date().toFormatString(DEFAULT_CURRENT_TIME_FORMAT)

    //메인 액티비티 상태
    private val _mainState = MutableStateFlow(MainState())
    val mainState: StateFlow<MainState> get() = _mainState.asStateFlow()

    private val _mainEvent = MutableSharedFlow<MainEvent>()
    val mainEvent = _mainEvent.asSharedFlow()

    //1단계 상태
    private val _detectFaceState = MutableStateFlow(DetectFaceState())
    val detectFaceState = _detectFaceState.asStateFlow()

    private val _detectedFaceIndexes = MutableStateFlow<List<Int>>(emptyList())
    val detectedFaceIndexes: StateFlow<List<Int>> = _detectedFaceIndexes.asStateFlow()

    //2단계 상태
    private val _mosaicFaceState = MutableStateFlow(MosaicFaceState())
    val mosaicFaceState = _mosaicFaceState.asStateFlow()

    init {
        initImage()
        getFaceList()
    }

    fun handleBackPress() {
        when (mainState.value.screenStep) {
            ScreenStep.SelectFace -> {
                viewModelScope.launch {
                    _mainEvent.emit(MainEvent.Finish)
                }
            }

            ScreenStep.MosaicFace -> {
                backPress()
                viewModelScope.launch {
                    setScreen(ScreenStep.SelectFace)
                    setImageViewType(PictureState.ViewType.Original)
                }
            }
        }
    }

    private fun initImage() {
        val image = File(savedStateHandle.get<String>(Extras.ImagePath) ?: "")
        _mainState.updateState {
            copy(
                pictureState = pictureState.copy(
                    originalImage = image,
                ),
                screenStep = ScreenStep.SelectFace
            )
        }
    }

    fun nextScreenStep() {
        viewModelScope.launch {
            with(_mainState.value) {
                when (screenStep) {
                    ScreenStep.SelectFace -> {
                        _mainState.updateState { copy(screenStep = ScreenStep.MosaicFace) }
                        _mainEvent.emit(MainEvent.NavigateToMosaicFace)
                    }

                    ScreenStep.MosaicFace -> {
                        _mainEvent.emit(MainEvent.NavigateToMosaicResult)
                    }
                }
            }
        }
    }

    private fun setScreen(screenStep: ScreenStep) {
        _mainState.updateState {
            copy(
                screenStep = screenStep,
            )
        }
    }

    fun setOnClickAllSelectButton() {
        _detectedFaceIndexes.value.copy {
            if (size == _detectFaceState.value.faceViewItem.faceList.size || size >= 1) {
                clear()
            } else {
                clear()
                addAll(_detectFaceState.value.faceViewItem.faceList.indices)
            }
        }.also { _detectedFaceIndexes.value = it }
    }

    private fun setImageViewType(viewType: PictureState.ViewType) {
        _mainState.updateState {
            copy(
                pictureState = pictureState.copy(
                    viewType = viewType
                )
            )
        }
    }

    fun setOnClickItem(index: Int) {
        _detectedFaceIndexes.value.copy {
            if (contains(index)) {
                remove(index)
            } else {
                add(index)
            }
        }.also { _detectedFaceIndexes.value = it }
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

    private fun setDetectFaceLoading(isLoading: Boolean) {
        _detectFaceState.updateState {
            copy(isLoading = isLoading)
        }
    }

    private fun getFaceList() {
        viewModelScope.launch {
            setDetectFaceLoading(true)
            setOnClearDetectedFaceIndex()
            with(_detectFaceState.value) {
                getDetectedFaceUseCase(
                    currentTime = currentTime,
                    threshold = detectStrength.toThreshold,
                    image = _mainState.value.pictureState.originalImage
                ).onSuccess { data ->
                    data.toFaceViewItem().also { faceViewItem ->
                        setFaceViewItem(faceViewItem)
                    }
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
                    _mainState.updateState {
                        copy(
                            pictureState = pictureState.copy(
                                viewType = PictureState.ViewType.Mosaic,
                                mosaicImage = it.mosaicImage, //이녀석이 다른 url로 늘 내려와야 한다
                            )
                        )
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
        viewModelScope.launch {
            if (exception is IcecNetworkException) {
                Log.d("실패", exception.toString())
                _mainEvent.emit(MainEvent.SendToast(exception.message))
            }
        }
    }

    companion object {
        private const val DEFAULT_CURRENT_TIME_FORMAT = "yyyy-MM-dd-HHmmss"
    }

}
