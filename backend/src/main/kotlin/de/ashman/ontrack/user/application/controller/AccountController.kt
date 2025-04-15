package de.ashman.ontrack.user.application.controller

import com.google.firebase.auth.FirebaseToken
import de.ashman.ontrack.errorhandling.ValidationException
import de.ashman.ontrack.user.infrastructure.UserRepository
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val userRepository: UserRepository
) {
    @GetMapping("/account")
    fun getCurrentUser(@AuthenticationPrincipal token: FirebaseToken): ResponseEntity<UserDto> {
        val user = userRepository.getReferenceById(token.uid)

        return ResponseEntity.ok(user.toDto())
    }

    @PostMapping("/account")
    @Transactional
    fun updateAccountSettings(
        @AuthenticationPrincipal token: FirebaseToken,
        @RequestBody @Valid accountSettings: AccountSettingsDto
    ): ResponseEntity<String> {
        val user = userRepository.getReferenceById(token.uid)
        val usernameChanged = !user.username.equals(accountSettings.username)
        if (usernameChanged && userRepository.existsUserByUsername(accountSettings.username)) {
            throw ValidationException("username", "Username is taken")
        }

        user.updateAccountSettings(
            name = accountSettings.name,
            username = accountSettings.username
        )

        return ResponseEntity.ok().build()
    }

    @PostMapping("/account/profile-picture")
    @Transactional
    fun changeProfilePicture(
        @AuthenticationPrincipal token: FirebaseToken,
        @RequestBody profilePictureUrl: String,
    ): ResponseEntity<Unit> {
        val user = userRepository.getReferenceById(token.uid)
        user.changeProfilePicture(profilePictureUrl)

        return ResponseEntity.ok().build()
    }
}
