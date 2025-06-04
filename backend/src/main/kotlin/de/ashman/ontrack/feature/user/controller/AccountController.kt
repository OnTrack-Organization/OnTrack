package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.AccountDto
import de.ashman.ontrack.feature.user.controller.dto.AccountSettingsDto
import de.ashman.ontrack.feature.user.controller.dto.SignInDto
import de.ashman.ontrack.feature.user.controller.dto.toAccountDto
import de.ashman.ontrack.feature.user.service.AccountService
import jakarta.transaction.Transactional
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
    @Transactional
    fun signIn(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody signInDto: SignInDto
    ): ResponseEntity<AccountDto> {
        val user = accountService.signInOrCreate(identity, signInDto.fcmToken)
        val status = if (user.createdAt == user.updatedAt) HttpStatus.CREATED else HttpStatus.OK

        return ResponseEntity.status(status).body(user.toAccountDto())
    }

    @PostMapping("/sign-out")
    @Transactional
    fun signOut(@AuthenticationPrincipal identity: Identity): ResponseEntity<Unit> {
        accountService.signOut(identity.id)

        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getCurrentAccount(@AuthenticationPrincipal identity: Identity): ResponseEntity<AccountDto> {
        val user = accountService.getCurrentAccount(identity.id)

        return ResponseEntity.ok(user)
    }

    @PostMapping("/settings")
    @Transactional
    fun updateAccountSettings(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody @Valid accountSettings: AccountSettingsDto
    ): ResponseEntity<String> {
        val result = accountService.updateAccountSettings(
            userId = identity.id,
            name = accountSettings.name,
            username = accountSettings.username
        )

        return if (result == null) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(result)
        }
    }

    @PostMapping("/profile-picture")
    @Transactional
    fun updateProfilePicture(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody profilePictureUrl: String,
    ): ResponseEntity<Unit> {
        accountService.updateProfilePicture(identity.id, profilePictureUrl)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping
    @Transactional
    fun deleteAccount(@AuthenticationPrincipal identity: Identity): ResponseEntity<Unit> {
        accountService.deleteAccount(identity.id)

        return ResponseEntity.ok().build()
    }
}
