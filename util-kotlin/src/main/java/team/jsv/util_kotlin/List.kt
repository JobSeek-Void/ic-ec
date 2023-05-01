package team.jsv.util_kotlin

inline fun <T> List<T>.copy(mutator: MutableList<T>.() -> Unit): List<T> {
    return toMutableList().apply(mutator)
}
