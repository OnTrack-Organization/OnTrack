package de.ashman.ontrack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.login.ui.GoogleLoginButton
import de.ashman.ontrack.login.ui.UserViewModel
import dev.gitlive.firebase.auth.FirebaseUser
import org.koin.compose.koinInject

@Composable
fun LoginTest(
    userViewModel: UserViewModel = koinInject(),
) {
    val userState by userViewModel.uiState.collectAsState()

    Column {
        val onFirebaseResult: (Result<FirebaseUser?>) -> Unit = { result ->
            if (result.isSuccess) {
                val firebaseUser = result.getOrNull()
                if (firebaseUser != null) {
                    userViewModel.saveUser(firebaseUser)
                }
            } else {
                println("Error Result: ${result.exceptionOrNull()?.message}")
            }
        }

        userState.user?.let {
            Text(
                text = it.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
            )
        }

        HorizontalDivider(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        GoogleLoginButton(
            modifier = Modifier.width(IntrinsicSize.Max),
            onFirebaseResult = onFirebaseResult,
        )
    }
}