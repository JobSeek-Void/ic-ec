package team.jsv.icec.ui.main.mosaic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import team.jsv.domain.model.Face
import team.jsv.domain.usecase.GetFaceListUseCase
import team.jsv.util_kotlin.IcecNetworkException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MosaicViewModel @Inject constructor(
    private val getFaceListUseCase: GetFaceListUseCase
) : ViewModel() {

    private val _detectFaces = MutableLiveData<Face>()
    val detectFaces: LiveData<Face> get() = _detectFaces

    private val _originalImage = MutableLiveData<File>()
    val originalImage: LiveData<File> get() = _originalImage

    private val _event = MutableLiveData<MosaicEvent>()
    val event: LiveData<MosaicEvent> get() = _event

    private val currentTime: String
        get() = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale("ko", "KR"))
            .format(System.currentTimeMillis())

    fun setImage(data: File) {
        _originalImage.value = data
    }

    fun getFaceList(
        image: File
    ) = viewModelScope.launch {
        getFaceListUseCase(
            currentTime = currentTime,
            image = image
        ).onSuccess {
            _detectFaces.postValue(it)
        }.onFailure {
            if (it is IcecNetworkException) {
                Log.d("실패", it.toString())
            }
            _event.postValue(MosaicEvent.SendToast(it.message.toString()))
        }
    }

}