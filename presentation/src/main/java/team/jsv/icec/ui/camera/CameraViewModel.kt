package team.jsv.icec.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import team.jsv.icec.base.BaseViewModel


class CameraViewModel : BaseViewModel() {
    private val _ratioState = MutableLiveData<Int>()
    val ratioState: LiveData<Int> get() = _ratioState

    fun initSetRatio() {
        _ratioState.value = SettingRatio.RATIO_1_1.id
    }

    fun setRatioState(value: Int) {
        _ratioState.value = value
    }
}