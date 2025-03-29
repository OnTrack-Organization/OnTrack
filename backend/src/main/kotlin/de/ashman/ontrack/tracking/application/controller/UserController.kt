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
    fun auth(
        @RequestHeader("Authorization", required = false) authHeader: String?
    ): ResponseEntity<Unit> {
        if (authHeader.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        println("Received Authorization header: $authHeader")

        val idToken = authHeader.removePrefix("Bearer ")

        val token: FirebaseToken = try {
            firebaseAuthService.verifyIdToken(idToken)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return if (userRepository.existsUserByEmail(token.email)) {
            ResponseEntity.ok().build()
        } else {
            val user = User(
                name = token.name,
                // TODO username kommt von woanders
                username = token.name,
                email = token.email,
                imageUrl = token.picture,
                // TODO fcm token auch
                fcmToken = "",
            )
            userRepository.saveAndFlush(user)
            ResponseEntity.status(HttpStatus.CREATED).build()
        }
    }

    @GetMapping("/test")
    fun test(@AuthenticationPrincipal token: FirebaseToken): String {
        return "You are authenticated as ${token.name}"
    }

    @GetMapping("/test2")
    fun test2(): String {
        return "You are not authenticated"
    }
}
