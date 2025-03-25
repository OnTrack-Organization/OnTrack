package de.ashman.ontrack.security.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class FirebaseAuthService {
    fun verifyIdToken(idToken: String): FirebaseToken {
        return try {
            // Verify the ID token using FirebaseAuth
            FirebaseAuth.getInstance().verifyIdToken(idToken)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid token", e)
        }
    }
}
