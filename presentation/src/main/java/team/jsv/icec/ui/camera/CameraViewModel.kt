package team.jsv.icec.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import team.jsv.icec.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : BaseViewModel() {
    private val _ratioState = MutableLiveData<Int>()
    val ratioState: LiveData<Int> get() = _ratioState

    fun setRatioState(value: Int) {
        _ratioState.value = value
    }
}