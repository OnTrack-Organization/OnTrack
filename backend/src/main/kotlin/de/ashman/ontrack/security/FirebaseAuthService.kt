package de.ashman.ontrack.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class FirebaseAuthService {
    fun verifyIdToken(idToken: String): FirebaseToken {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(idToken)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid token", e)
        }
    }
}
