package team.jsv.icec.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import team.jsv.icec.base.BaseViewModel

class MosaicResultViewModel : BaseViewModel() {
    private val _image = MutableLiveData<Uri>()
    val image: LiveData<Uri> get() = _image

    fun setImage(image: Uri) {
        _image.value = image
    }
}