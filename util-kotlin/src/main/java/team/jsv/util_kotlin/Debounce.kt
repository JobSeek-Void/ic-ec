@file:OptIn(FlowPreview::class)

package team.jsv.util_kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Suppress("FunctionName")
fun <T> MutableDebounceFlow(
    action: MutableSharedFlow<T>.() -> Unit = {},
) = MutableSharedFlow<T>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
).apply(action)

fun <T> MutableSharedFlow<T>.debounceAction(
    coroutineScope: CoroutineScope,
    timeoutMillis: Long,
    builder: Flow<T>.() -> Flow<T> = { this },
    action: suspend (T) -> Unit,
) {
    coroutineScope.launch {
        debounce(timeoutMillis).builder().collectLatest(action)
    }
}