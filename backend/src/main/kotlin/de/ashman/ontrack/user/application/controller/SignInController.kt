package de.ashman.ontrack.user.application.controller

import com.google.firebase.auth.FirebaseToken
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
        @AuthenticationPrincipal token: FirebaseToken,
        @RequestBody signInDto: SignInDto
    ): ResponseEntity<UserDto> {
        var user = userRepository.findOneByEmail(token.email)

        if (user != null) {
            user.updateFcmToken(signInDto.fcmToken)
            return ResponseEntity.ok(user.toDto())
        }

        user = User(
            id = token.uid,
            name = token.name,
            email = token.email,
            profilePictureUrl = token.picture
        )

        user.updateFcmToken(signInDto.fcmToken)
        userRepository.save(user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(user.toDto())
    }

    @PostMapping("/sign-out")
    @Transactional
    fun signOut(@AuthenticationPrincipal token: FirebaseToken): ResponseEntity<Unit> {
        val user = userRepository.getReferenceById(token.uid)
        user.clearFcmToken()

        return ResponseEntity.ok().build()
    }
}
