package team.jsv.icec.ui.main.mosaic.detect

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import team.jsv.icec.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DetectFaceViewModel @Inject constructor() : BaseViewModel() {

    private val _isAllSelect = MutableStateFlow(false)
    val isAllSelect: StateFlow<Boolean> = _isAllSelect.asStateFlow()

    fun setOnGroupSelectButton() {
        _isAllSelect.value = !_isAllSelect.value
    }

}