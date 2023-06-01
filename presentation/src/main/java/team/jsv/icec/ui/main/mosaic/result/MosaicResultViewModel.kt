package team.jsv.icec.ui.main.mosaic.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import team.jsv.icec.base.BaseViewModel

class MosaicResultViewModel : BaseViewModel() {

    private val _image = MutableLiveData<String>()
    val image: LiveData<String> get() = _image

    fun setImage(image: String) {
        _image.value = image
    }

}