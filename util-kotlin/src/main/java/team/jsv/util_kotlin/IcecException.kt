package team.jsv.util_kotlin

sealed class IcecException(override val message: String) : IllegalStateException(message)

class IcecNetworkException(
    val code: String,
    override val message: String,
) : IcecException(message) {
    override fun toString(): String {
        return "NetworkError(code=$code, message=$message)"
    }
}

class IcecClientException(
    override val message: String,
) : IcecException(message) {
    override fun toString(): String {
        return "Client Error : $message)"
    }
}
