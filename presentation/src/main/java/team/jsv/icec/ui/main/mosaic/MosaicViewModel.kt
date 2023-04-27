package team.jsv.icec.ui.main.mosaic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import team.jsv.domain.usecase.GetDetectedFaceUseCase
import team.jsv.domain.usecase.GetMosaicImageUseCase
import team.jsv.icec.base.BaseViewModel
import team.jsv.icec.base.Event
import team.jsv.icec.ui.main.mosaic.detect.model.FaceViewItem
import team.jsv.icec.ui.main.mosaic.detect.model.toFaceViewItem
import team.jsv.util_kotlin.IcecNetworkException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

enum class ScreenStep {
    SelectMosaicEdit,
    SelectFace,
    MosaicFace;
}

@HiltViewModel
internal class MosaicViewModel @Inject constructor(
    private val getDetectedFaceUseCase: GetDetectedFaceUseCase,
    private val getMosaicImageUseCase: GetMosaicImageUseCase
) : BaseViewModel() {

    companion object {
        const val DEFAULT_MOSAIC_STRENGTH = 20f
    }

    private val currentTime: String =
        SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale("ko", "KR"))
            .format(System.currentTimeMillis())

    private val _detectFaces = MutableStateFlow(FaceViewItem())
    val detectFaces: StateFlow<FaceViewItem> = _detectFaces.asStateFlow()

    private val _originalImage = MutableLiveData<File>()
    val originalImage: LiveData<File> get() = _originalImage

    private val _mosaicImage = MutableLiveData<Event<String>>()
    val mosaicImage: LiveData<Event<String>> get() = _mosaicImage

    private val _pixelSize = MutableLiveData<Float>().apply { postValue(DEFAULT_MOSAIC_STRENGTH) }
    val pixelSize: LiveData<Float> get() = _pixelSize

    private val _screenStep =
        MutableLiveData<ScreenStep>().apply { postValue(ScreenStep.SelectMosaicEdit) }
    val screenStep: LiveData<ScreenStep> get() = _screenStep

    private val _mosaicEvent = MutableLiveData<Event<MosaicEvent>>()
    val mosaicEvent: LiveData<Event<MosaicEvent>> get() = _mosaicEvent

    private val _state = MutableLiveData<PictureState>()
    val state: LiveData<PictureState> get() = _state

    private val _selectedItemIndex = MutableSharedFlow<MutableList<Int>>(replay = 1)
    val selectedItemIndex: SharedFlow<MutableList<Int>> = _selectedItemIndex.asSharedFlow()

    fun setScreen(screenStep: ScreenStep) = _screenStep.postValue(screenStep)

    fun setImage(data: File) {
        _originalImage.value = data
        _state.postValue(PictureState.File(data))
    }

    fun setPixelSize(value: Float) {
        _pixelSize.value = value
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
            val currentItemIndexList =
                _selectedItemIndex.replayCache.firstOrNull() ?: mutableListOf()
            if (currentItemIndexList.size == detectFaces.value.faceList.size ||
                    currentItemIndexList.size >= 1) {
                currentItemIndexList.clear()
            } else {
                currentItemIndexList.clear()
                currentItemIndexList.addAll(detectFaces.value.faceList.indices)
            }
            _selectedItemIndex.emit(currentItemIndexList)
        }
    }

    fun setOnClickItem(index: Int) {
        viewModelScope.launch {
            val currentItemIndexList =
                _selectedItemIndex.replayCache.firstOrNull() ?: mutableListOf()
            if (currentItemIndexList.contains(index)) {
                currentItemIndexList.remove(index)
            } else {
                currentItemIndexList.add(index)
            }
            _selectedItemIndex.emit(currentItemIndexList)
        }
    }

    internal fun getFaceList(image: File) {
        viewModelScope.launch {
            getDetectedFaceUseCase(
                currentTime = currentTime,
                threshold = 0.1f,
                image = image
            ).onSuccess {
                _detectFaces.value = it.toFaceViewItem()
            }.onFailure {
                handleException(it)
            }
        }
    }

    internal fun getMosaicImage() {
        viewModelScope.launch {
            val originalImage = detectFaces.value.originalImage
            val indexes = selectedItemIndex.replayCache.firstOrNull() ?: mutableListOf()
            val coordinates = indexes.map { detectFaces.value.faceList[it].coordinates }
                .ifEmpty { listOf(listOf()) }

            pixelSize.value?.let { pixelSize ->
                getMosaicImageUseCase(
                    currentTime = currentTime,
                    pixelSize = pixelSize.toInt(),
                    originalImage = originalImage,
                    coordinates = coordinates
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

    private fun handleException(exception: Throwable) {
        if (exception is IcecNetworkException) {
            Log.d("실패", exception.toString())
            _mosaicEvent.postValue(Event(MosaicEvent.SendToast(exception.message)))
        }
    }
}
