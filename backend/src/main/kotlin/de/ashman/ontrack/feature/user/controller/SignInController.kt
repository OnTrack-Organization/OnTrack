package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.AccountDto
import de.ashman.ontrack.feature.user.controller.dto.toAccountDto
import de.ashman.ontrack.feature.user.controller.dto.SignInDto
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserService
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SignInController(
    private val userService: UserService,
) {
    @PostMapping("/sign-in")
    @Transactional
    fun signIn(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody signInDto: SignInDto
    ): ResponseEntity<AccountDto> {
        var user = userService.findByEmail(identity.email)

        if (user != null) {
            user.updateFcmToken(signInDto.fcmToken)
            return ResponseEntity.ok(user.toAccountDto())
        }

        user = User(
            id = identity.id,
            email = identity.email,
            name = identity.name.orEmpty(),
            profilePictureUrl = identity.picture.orEmpty(),
        )

        user.updateFcmToken(signInDto.fcmToken)
        userService.save(user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(user.toAccountDto())
    }

    @PostMapping("/sign-out")
    @Transactional
    fun signOut(@AuthenticationPrincipal identity: Identity): ResponseEntity<Unit> {
        val user = userService.getById(identity.id)

        user.clearFcmToken()

        return ResponseEntity.ok().build()
    }
}
