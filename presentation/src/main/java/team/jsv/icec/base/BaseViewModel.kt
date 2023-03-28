package team.jsv.icec.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private val _backPress = MutableLiveData<Event<Unit>>()
    val backPress: LiveData<Event<Unit>> get() = _backPress

    fun backPress() {
        _backPress.value = Event(Unit)
    }

}

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * 이벤트의 내용을 반환하고, 그 내용이 처리되었음을 표시합니다.
     * 내용이 이미 처리되었다면 null을 반환합니다.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 이벤트의 내용을 반환합니다.
     * 이 메서드는 이벤트 처리 여부에 상관없이 내용을 반환합니다.
     */
    fun peekContent(): T = content
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}