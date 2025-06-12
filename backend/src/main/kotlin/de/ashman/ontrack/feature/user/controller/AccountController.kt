package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.*
import de.ashman.ontrack.feature.user.service.AccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class AccountController(
    private val accountService: AccountService
) {
    @PostMapping("/sign-in")
    fun signIn(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody signInDto: SignInDto
    ): ResponseEntity<AccountDto> {
        val user = accountService.signInOrCreate(identity, signInDto.fcmToken)
        val status = if (user.createdAt == user.updatedAt) HttpStatus.CREATED else HttpStatus.OK

        return ResponseEntity.status(status).body(user.toAccountDto())
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signOut(
        @AuthenticationPrincipal identity: Identity
    ) {
        accountService.signOut(identity.id)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentAccount(
        @AuthenticationPrincipal identity: Identity
    ): AccountDto {
        return accountService.getCurrentAccount(identity.id)
    }

    @PostMapping("/settings")
    fun updateAccountSettings(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody @Valid accountSettings: AccountSettingsDto
    ): ResponseEntity<AccountSettingsResponseDto> {
        return try {
            val updatedUser = accountService.updateAccountSettings(
                userId = identity.id,
                name = accountSettings.name,
                username = accountSettings.username
            )
            ResponseEntity.ok(AccountSettingsResponseDto(user = updatedUser, error = null))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body(AccountSettingsResponseDto(user = null, error = e.message))
        }
    }

    @PostMapping("/profile-picture")
    fun updateProfilePicture(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody profilePictureUrl: String
    ): UserDto {
        return accountService.updateProfilePicture(identity.id, profilePictureUrl)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    fun deleteAccount(
        @AuthenticationPrincipal identity: Identity
    ) {
        accountService.deleteAccount(identity.id)
    }
}
