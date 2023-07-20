package team.jsv.icec.ui.main.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import team.jsv.icec.base.BaseViewModel

class StartViewModel : BaseViewModel() {
    private val _imagePaths = MutableLiveData<List<String>>()
    val imagePaths: LiveData<List<String>> get() = _imagePaths

    fun setImagePaths(imagePathsList: List<String>) {
        _imagePaths.value = imagePathsList
    }
}