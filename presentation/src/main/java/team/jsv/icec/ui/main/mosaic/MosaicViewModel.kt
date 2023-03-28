package team.jsv.icec.ui.main.mosaic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import team.jsv.domain.model.Face
import team.jsv.domain.usecase.GetDetectedFaceUseCase
import team.jsv.domain.usecase.GetMosaicImageUseCase
import team.jsv.icec.base.BaseViewModel
import team.jsv.icec.base.Event
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

    private val currentTime: String =
        SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale("ko", "KR"))
            .format(System.currentTimeMillis())

    private val _detectFaces = MutableLiveData<Face>()
    val detectFaces: LiveData<Face> get() = _detectFaces

    private val _originalImage = MutableLiveData<File>()
    val originalImage: LiveData<File> get() = _originalImage

    private val _mosaicImage = MutableLiveData<Event<String>>()
    val mosaicImage: LiveData<Event<String>> get() = _mosaicImage

    private val _pixelSize = MutableLiveData<Float>().apply { postValue(20f) }
    val pixelSize: LiveData<Float> get() = _pixelSize

    private val _screenStep =
        MutableLiveData<ScreenStep>().apply { postValue(ScreenStep.SelectMosaicEdit) }
    val screenStep: LiveData<ScreenStep> get() = _screenStep

    private val _mosaicEvent = MutableLiveData<Event<MosaicEvent>>()
    val mosaicEvent: LiveData<Event<MosaicEvent>> get() = _mosaicEvent

    private val _state = MutableLiveData<PictureState>()
    val state: LiveData<PictureState> get() = _state

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
                setScreen(ScreenStep.SelectFace)
            }
            ScreenStep.SelectFace -> {
                setScreen(ScreenStep.SelectMosaicEdit)
            }
            else -> {}
        }
    }

    internal fun getFaceList(
        image: File
    ) = viewModelScope.launch {
        getDetectedFaceUseCase(
            currentTime = currentTime,
            image = image
        ).onSuccess {
            _detectFaces.postValue(it)
        }.onFailure {
            handleException(it)
        }
    }

    internal fun getMosaicImage() =
        viewModelScope.launch {
            val originalImage = detectFaces.value?.originalImage ?: ""
            val coordinates = arrayListOf<List<Int>>().also { coordinates ->
                detectFaces.value?.faceList?.forEach {
                    coordinates.add(it.coordinates.toList())
                }
            }

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

    private fun handleException(exception: Throwable) {
        if (exception is IcecNetworkException) {
            Log.d("실패", exception.toString())
        }
        _mosaicEvent.postValue(Event(MosaicEvent.SendToast(exception.message.toString())))
    }
}
