package de.ashman.ontrack.features.settings

import de.ashman.ontrack.repository.firestore.FirestoreUserRepository

class UsernameValidationUseCase(
    private val firestoreUserRepository: FirestoreUserRepository
) {

    suspend fun validate(newUsername: String, currentUsername: String? = null): UsernameValidationResult {
        if (newUsername.isBlank()) return UsernameValidationResult.Invalid(UsernameError.EMPTY)

        if (newUsername.contains(" ")) return UsernameValidationResult.Invalid(UsernameError.WHITESPACE)

        if (newUsername.length < 5) return UsernameValidationResult.Invalid(UsernameError.TOO_SHORT)

        if (newUsername.length > 25) return UsernameValidationResult.Invalid(UsernameError.TOO_LONG)

        if (newUsername.any { it.isUpperCase() }) return UsernameValidationResult.Invalid(UsernameError.NO_UPPERCASE)

        val allowedPattern = "^[a-z0-9_.]*$".toRegex()
        if (!allowedPattern.matches(newUsername)) return UsernameValidationResult.Invalid(UsernameError.INVALID_CHARACTERS)

        // Only check availability if the username has changed
        if (newUsername != currentUsername && firestoreUserRepository.isUsernameTaken(newUsername)) return UsernameValidationResult.Invalid(UsernameError.TAKEN)

        return UsernameValidationResult.Valid
    }
}

sealed class UsernameValidationResult {
    object Valid : UsernameValidationResult()
    data class Invalid(val error: UsernameError) : UsernameValidationResult()
}

enum class UsernameError {
    EMPTY,
    WHITESPACE,
    TAKEN,
    TOO_LONG,
    TOO_SHORT,
    NO_UPPERCASE,
    INVALID_CHARACTERS
}
