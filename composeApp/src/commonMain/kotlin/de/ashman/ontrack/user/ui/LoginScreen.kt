package de.ashman.ontrack.user.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinInject(),
    onLoginSuccess: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        GoogleLoginButton { result ->
            if (result.isSuccess) {
                val firebaseUser = result.getOrNull()
                if (firebaseUser != null) {
                    userViewModel.registerUser(firebaseUser)
                    onLoginSuccess()
                }
            } else {
                // TODO anders handeln
                println("Error Result: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}