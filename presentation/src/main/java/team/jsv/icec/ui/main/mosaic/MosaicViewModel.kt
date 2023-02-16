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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MosaicViewModel @Inject constructor(
    private val getFaceListUseCase: GetFaceListUseCase
) : ViewModel() {

    private val _faces = MutableLiveData<List<Face>>()
    val faces: LiveData<List<Face>> get() = _faces
    private val _originalImage = MutableLiveData<File>()
    val originalImage: LiveData<File> get() = _originalImage

    private val currentTime = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale("ko", "KR"))
        .format(System.currentTimeMillis())

    fun setImage(data: File) {
        _originalImage.value = data
    }

    fun getFaceList(
        image: File
    ) = viewModelScope.launch {
        getFaceListUseCase(
            currentTime = currentTime.toString(),
            image = image
        ).onSuccess {
            _faces.postValue(it.toList())
        }.onFailure {
            Log.d("실패", it.message.toString())
        }
    }

}