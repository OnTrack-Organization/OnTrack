package de.ashman.ontrack.network.services.account

enum class UsernameError {
    TAKEN,
    EMPTY,
    WHITESPACE,
    TOO_LONG,
    TOO_SHORT,
    NO_UPPERCASE,
    INVALID_CHARACTERS
}

fun mapUsernameError(errorMessage: String?): UsernameError? {
    return when (errorMessage) {
        "USERNAME_EMPTY" -> UsernameError.EMPTY
        "USERNAME_WHITESPACE" -> UsernameError.WHITESPACE
        "USERNAME_TOO_LONG" -> UsernameError.TOO_LONG
        "USERNAME_TOO_SHORT" -> UsernameError.TOO_SHORT
        "USERNAME_NO_UPPERCASE" -> UsernameError.NO_UPPERCASE
        "USERNAME_INVALID_CHARACTERS" -> UsernameError.INVALID_CHARACTERS
        "USERNAME_TAKEN" -> UsernameError.TAKEN
        else -> null
    }
}
