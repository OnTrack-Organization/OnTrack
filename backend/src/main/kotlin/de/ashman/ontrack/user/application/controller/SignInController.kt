package de.ashman.ontrack.user.application.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.user.domain.User
import de.ashman.ontrack.user.infrastructure.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SignInController(
    private val userRepository: UserRepository,
) {
    @PostMapping("/sign-in")
    @Transactional
    fun signIn(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody signInDto: SignInDto
    ): ResponseEntity<UserDto> {
        var user = userRepository.findOneByEmail(identity.email)

        if (user != null) {
            user.updateFcmToken(signInDto.fcmToken)
            return ResponseEntity.ok(user.toDto())
        }

        user = User(
            id = identity.id,
            name = identity.name,
            email = identity.email,
            profilePictureUrl = identity.picture
        )

        user.updateFcmToken(signInDto.fcmToken)
        userRepository.save(user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(user.toDto())
    }

    @PostMapping("/sign-out")
    @Transactional
    fun signOut(@AuthenticationPrincipal identity: Identity): ResponseEntity<Unit> {
        val user = userRepository.getReferenceById(identity.id)
        user.clearFcmToken()

        return ResponseEntity.ok().build()
    }
}
