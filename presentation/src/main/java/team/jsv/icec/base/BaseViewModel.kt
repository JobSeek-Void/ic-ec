package team.jsv.icec.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private val _backPress = MutableLiveData<Event<Unit>>()
    val backPress: LiveData<Event<Unit>> get() = _backPress

    fun backPress() {
        _backPress.value = Event(Unit)
    }

}
