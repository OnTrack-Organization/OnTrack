package de.ashman.ontrack.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.stereotype.Component

@Component
class FirebaseInitializer {
    init {
        val cred = GoogleCredentials.getApplicationDefault()

        val options = FirebaseOptions.builder()
            // Looks for Google credentials in env GOOGLE_APPLICATION_CREDENTIALS
            .setCredentials(cred)
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
    }
}
