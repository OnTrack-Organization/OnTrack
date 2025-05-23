package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.AccountDto
import de.ashman.ontrack.feature.user.controller.dto.AccountSettingsDto
import de.ashman.ontrack.feature.user.controller.dto.toAccountDto
import de.ashman.ontrack.feature.user.repository.UserService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val userService: UserService
) {
    @GetMapping("/account")
    fun getCurrentUser(@AuthenticationPrincipal identity: Identity): ResponseEntity<AccountDto> {
        val user = userService.getById(identity.id)

        return ResponseEntity.ok(user.toAccountDto())
    }

    @PostMapping("/account")
    @Transactional
    fun updateAccountSettings(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody @Valid accountSettings: AccountSettingsDto
    ): ResponseEntity<String> {
        val validationResult = validateUsername(accountSettings.username)

        if (validationResult != null) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(validationResult)
        }

        val user = userService.getById(identity.id)

        user.updateAccountSettings(
            name = accountSettings.name,
            username = accountSettings.username
        )

        return ResponseEntity.ok().build()
    }

    @PostMapping("/account/profile-picture")
    @Transactional
    fun changeProfilePicture(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody profilePictureUrl: String,
    ): ResponseEntity<Unit> {
        val user = userService.getById(identity.id)

        user.changeProfilePicture(profilePictureUrl)

        return ResponseEntity.ok().build()
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
