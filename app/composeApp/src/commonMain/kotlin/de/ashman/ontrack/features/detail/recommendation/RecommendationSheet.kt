package de.ashman.ontrack.features.detail.recommendation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SendMessageTextField
import de.ashman.ontrack.features.detail.DetailResultState
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.recommendation_friends_empty
import ontrack.composeapp.generated.resources.recommendation_friends_error
import ontrack.composeapp.generated.resources.recommendation_sent
import ontrack.composeapp.generated.resources.recommendation_textfield_placeholder
import ontrack.composeapp.generated.resources.recommendation_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecommendationSheet(
    resultState: DetailResultState,
    friends: List<User>,
    sentRecommendations: List<Recommendation>,
    fetchSentRecommendations: (String) -> Unit,
    onSendRecommendation: (String, String?) -> Unit,
    onClickUser: (String) -> Unit,
) {
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    val isAnyUserSelected = selectedUserId != null
    var message by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.recommendation_title),
            style = MaterialTheme.typography.titleMedium,
        )

        when (resultState) {
            DetailResultState.Loading -> {
            }

            DetailResultState.Success -> {
                if (friends.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(Res.string.recommendation_friends_empty),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    return@Column
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(friends) { user ->
                        FriendRecommendSelectorIcon(
                            userId = user.id,
                            profilePictureUrl = user.profilePictureUrl,
                            username = user.username,
                            isSelected = selectedUserId == user.id,
                            isAnyUserSelected = isAnyUserSelected,
                            onSelectUser = { id ->
                                selectedUserId = id

                                id?.let {
                                    fetchSentRecommendations(it)
                                }
                            }
                        )
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier
                        .weight(1f, false)
                        .heightIn(max = 200.dp)
                        .padding(horizontal = 16.dp),
                    visible = sentRecommendations.isNotEmpty() && isAnyUserSelected,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.recommendation_sent),
                            style = MaterialTheme.typography.titleMedium,
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(sentRecommendations) { recommendation ->
                                RecommendationCard(
                                    profilePictureUrl = recommendation.user.profilePictureUrl,
                                    username = recommendation.user.username,
                                    timestamp = recommendation.timestamp,
                                    message = recommendation.message,
                                    onClickUser = { onClickUser(recommendation.user.id) },
                                )
                            }
                        }
                    }
                }

                SendMessageTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    placeholder = stringResource(Res.string.recommendation_textfield_placeholder),
                    value = message,
                    onValueChange = { message = it },
                    isSendVisible = isAnyUserSelected,
                    isSending = resultState == DetailResultState.Loading,
                    onSend = {
                        selectedUserId?.let {
                            onSendRecommendation(it, message.text)
                        }
                    },
                )
            }

            DetailResultState.Error -> {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(Res.string.recommendation_friends_error),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FriendRecommendSelectorIcon(
    userId: String,
    profilePictureUrl: String?,
    username: String,
    isSelected: Boolean,
    isAnyUserSelected: Boolean,
    onSelectUser: (String?) -> Unit,
) {
    val shouldDim = isAnyUserSelected && !isSelected

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(64.dp),
        ) {
            PersonImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer { alpha = if (shouldDim) 0.5f else 1f },
                profilePictureUrl = profilePictureUrl,
                onClick = {
                    if (isSelected) {
                        onSelectUser(null)
                    } else {
                        onSelectUser(userId)
                    }
                }
            )

            if (isSelected) {
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check Icon",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }

        Text(
            text = username,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .graphicsLayer { alpha = if (shouldDim) 0.5f else 1f }
                .width(64.dp),
            textAlign = TextAlign.Center,
        )
    }
}
