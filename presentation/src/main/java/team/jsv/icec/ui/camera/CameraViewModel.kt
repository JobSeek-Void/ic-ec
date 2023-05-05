package team.jsv.icec.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import team.jsv.icec.base.BaseViewModel


class CameraViewModel : BaseViewModel() {
    private val _ratioState = MutableLiveData<Int>().apply { value = SettingRatio.RATIO_1_1.id }
    val ratioState: LiveData<Int> get() = _ratioState


    fun setRatioState(value: Int) {
        _ratioState.value = value
    }
}