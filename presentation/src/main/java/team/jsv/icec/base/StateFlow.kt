package team.jsv.icec.base

import kotlinx.coroutines.flow.MutableStateFlow


fun <S> MutableStateFlow<S>.updateState(
    reduce: S.() -> S,
) {
    val state = value.reduce()
    this.value = state
}