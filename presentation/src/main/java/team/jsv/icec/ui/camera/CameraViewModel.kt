package team.jsv.icec.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import team.jsv.icec.base.BaseViewModel


class CameraViewModel : BaseViewModel() {
    private val _ratioState = MutableLiveData<Int>().apply { value = SettingRatio.RATIO_1_1.id }
    val ratioState: LiveData<Int> get() = _ratioState

    private val _bitmapImage = MutableLiveData<Bitmap>()
    val bitmapImage: LiveData<Bitmap> get() = _bitmapImage

    private val _isFrontCamera = MutableLiveData<Boolean>().apply { value = false }
    val isFrontCamera: LiveData<Boolean> get() = _isFrontCamera

    fun setRatioState(value: Int) {
        _ratioState.value = value
    }

    fun setBitmapImage(image: Bitmap) {
        _bitmapImage.value = image
    }

    fun setIsFrontCamera(value : Boolean) {
        _isFrontCamera.value = value
    }
}