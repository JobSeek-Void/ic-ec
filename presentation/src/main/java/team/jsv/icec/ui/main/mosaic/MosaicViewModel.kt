package team.jsv.icec.ui.main.mosaic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MosaicViewModel : ViewModel() {

    private val image = MutableLiveData<Int>()

    fun setImage(data: Int) {
        image.value = data
    }

    fun getImage(): LiveData<Int> = image

    fun getFaceList(image: Int) {

    }
}