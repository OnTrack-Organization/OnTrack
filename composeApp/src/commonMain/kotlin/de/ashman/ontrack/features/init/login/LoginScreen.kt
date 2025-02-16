package de.ashman.ontrack.features.init.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import de.ashman.ontrack.authentication.AuthViewModel
import de.ashman.ontrack.domain.user.toDomain
import de.ashman.ontrack.features.common.OnTrackButton
import dev.gitlive.firebase.auth.FirebaseUser
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.apple
import ontrack.composeapp.generated.resources.apple_login_button
import ontrack.composeapp.generated.resources.google
import ontrack.composeapp.generated.resources.google_login_button
import ontrack.composeapp.generated.resources.join_title
import ontrack.composeapp.generated.resources.or_divider
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    onNavigateAfterLogin: () -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            LoginButtons(
                onClickContinue = { firebaseUser ->
                    viewModel.signUp(firebaseUser.toDomain())
                    onNavigateAfterLogin()
                }
            )
        }
    }
}

@Composable
fun LoginButtons(
    onClickContinue: (FirebaseUser) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.join_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        GoogleButtonUiContainerFirebase(
            linkAccount = false,
            onResult = { result ->
                result.getOrNull()?.let(onClickContinue)
                    ?: Logger.e("Error while logging in: ${result.exceptionOrNull()?.message}")
            }
        ) {
            OnTrackButton(
                text = Res.string.google_login_button,
                icon = vectorResource(Res.drawable.google),
                onClick = { onClick() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(Res.string.or_divider),
                style = MaterialTheme.typography.bodyMedium,
                color = DividerDefaults.color,
                fontWeight = FontWeight.Bold,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        AppleButtonUiContainer(
            linkAccount = false,
            onResult = { result ->
                result.getOrNull()?.let(onClickContinue)
                    ?: Logger.e("Error while logging in: ${result.exceptionOrNull()?.message}")
            }
        ) {
            OnTrackButton(
                text = Res.string.apple_login_button,
                icon = vectorResource(Res.drawable.apple),
                onClick = { onClick() }
            )
        }
    }
}