package team.jsv.icec.ui.main.mosaic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import team.jsv.domain.model.Face
import team.jsv.domain.usecase.GetBlurImageUseCase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MosaicViewModel @Inject constructor(
    private val getBlurImageUseCase: GetBlurImageUseCase
) : ViewModel() {

    private val _faces = MutableLiveData<List<Face>>()
    val faces: LiveData<List<Face>> get() = _faces
    private val _originalImage = MutableLiveData<File>()
    val originalImage: LiveData<File> get() = _originalImage
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val currentTime: String
        get() = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale("ko", "KR"))
            .format(System.currentTimeMillis())

    fun setImage(data: File) {
        _originalImage.value = data
    }

//    fun getFaceList(
//        image: File
//    ) = viewModelScope.launch {
//        getFaceListUseCase(
//            currentTime = currentTime,
//            image = image
//        ).onSuccess {
//            _faces.postValue(it.toList())
//            _errorMessage.value = null
//        }.onFailure {
//            Log.d("실패", it.message.toString())
//            _errorMessage.value = "다시 시도해주세요."
//        }
//    }

}