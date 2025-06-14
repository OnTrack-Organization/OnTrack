package de.ashman.ontrack.feature.user.service

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.AccountDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toAccountDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val userRepository: UserRepository,
) {
    fun getCurrentAccount(id: String): AccountDto = userRepository.getReferenceById(id).toAccountDto()

    @Transactional
    fun signInOrCreate(identity: Identity, fcmToken: String?): User {
        val existingUser = userRepository.findOneByEmail(identity.email)
        return if (existingUser != null) {
            existingUser.fcmToken = fcmToken
            existingUser
        } else {
            userRepository.save(
                User(
                    id = identity.id,
                    email = identity.email,
                    name = identity.name.orEmpty(),
                    profilePictureUrl = identity.picture.orEmpty(),
                    fcmToken = fcmToken
                )
            )
        }
    }

    @Transactional
    fun signOut(userId: String) {
        val user = userRepository.getReferenceById(userId)
        user.fcmToken = null
    }

    @Transactional
    fun updateAccountSettings(userId: String, name: String, username: String): UserDto {
        validateUsername(username)?.let { errorMessage ->
            throw IllegalArgumentException(errorMessage)
        }

        val user = userRepository.getReferenceById(userId)
        user.name = name
        user.username = username

        return user.toDto()
    }

    @Transactional
    fun updateProfilePicture(userId: String, profilePictureUrl: String): UserDto {
        val user = userRepository.getReferenceById(userId)
        user.profilePictureUrl = profilePictureUrl
        return user.toDto()
    }

    @Transactional
    fun deleteAccount(userId: String) {
        if (!userRepository.existsById(userId)) {
            throw EntityNotFoundException("User with ID $userId not found")
        }

        userRepository.deleteById(userId)
    }

    private fun validateUsername(username: String?): String? {
        if (username.isNullOrBlank()) return "USERNAME_EMPTY"
        if (username.contains(" ")) return "USERNAME_WHITESPACE"
        if (username.length < 5) return "USERNAME_TOO_SHORT"
        if (username.length > 25) return "USERNAME_TOO_LONG"
        if (username.any { it.isUpperCase() }) return "USERNAME_NO_UPPERCASE"

        val allowedPattern = "^[a-z0-9_.]*$".toRegex()
        if (!allowedPattern.matches(username)) return "USERNAME_INVALID_CHARACTERS"

        return null
    }
}