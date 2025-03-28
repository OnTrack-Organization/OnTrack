package de.ashman.ontrack.tracking.application.controller

import com.google.firebase.auth.FirebaseToken
import de.ashman.ontrack.security.service.FirebaseAuthService
import de.ashman.ontrack.tracking.domain.User
import de.ashman.ontrack.tracking.infrastructure.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userRepository: UserRepository,
    private val firebaseAuthService: FirebaseAuthService
) {
    @PostMapping("/auth")
    fun auth(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Unit> {
        val idToken = authHeader.removePrefix("Bearer ")

        val token = firebaseAuthService.verifyIdToken(idToken)
        val userExists = userRepository.existsUserByEmail(token.email)
        if (!userExists) {
            val user = User(
                "some Token",
                token.name,
                token.name,
                token.email,
                ""
            )
            userRepository.saveAndFlush(user)

            return ResponseEntity.status(HttpStatus.CREATED).build()
        }

        return ResponseEntity.ok().build()
    }

    @GetMapping("/test")
    fun test(@AuthenticationPrincipal token: FirebaseToken ): String
    {
       return "You are authenticated as ${token.name}"
    }

    @GetMapping("/test2")
    fun test2(): String
    {
        return "You are not authenticated"
    }
}
