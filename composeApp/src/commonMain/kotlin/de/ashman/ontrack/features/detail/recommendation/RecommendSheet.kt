package de.ashman.ontrack.features.detail.recommendation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.features.common.OnTrackIconButton
import de.ashman.ontrack.features.common.OnTrackTextField
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.formatDateTime
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_recommend_friends_empty
import ontrack.composeapp.generated.resources.detail_recommend_previous
import ontrack.composeapp.generated.resources.detail_recommend_textfield_placeholder
import ontrack.composeapp.generated.resources.detail_recommend_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecommendSheet(
    selectableFriends: List<Friend>,
    previousSentRecommendations: List<Recommendation>,
    onSendRecommendation: (String, String?) -> Unit,
    selectUser: (String) -> Unit,
    onClickUser: (String) -> Unit,
) {
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    val isAnyUserSelected = selectedUserId != null

    var message by remember { mutableStateOf("") }

    Column {
        Text(
            text = stringResource(Res.string.detail_recommend_title),
            style = MaterialTheme.typography.titleMedium,
        )

        if (selectableFriends.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = stringResource(Res.string.detail_recommend_friends_empty),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        LazyRow(
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            items(selectableFriends) { friend ->
                FriendRecommendSelectorIcon(
                    userId = friend.id,
                    imageUrl = friend.imageUrl,
                    username = friend.username,
                    isSelected = selectedUserId == friend.id,
                    isAnyUserSelected = isAnyUserSelected,
                    onSelectUser = { id ->
                        selectedUserId = id

                        selectedUserId?.let {
                            selectUser(it)
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .weight(1f, false)
                .heightIn(max = 200.dp),
            visible = previousSentRecommendations.isNotEmpty() && isAnyUserSelected,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.detail_recommend_previous),
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(previousSentRecommendations) { recommendation ->
                        RecommendationCard(
                            userImageUrl = recommendation.userImageUrl,
                            username = recommendation.username,
                            timestamp = recommendation.timestamp.formatDateTime(),
                            message = recommendation.message,
                            onClickUser = { onClickUser(recommendation.userId) },
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OnTrackTextField(
                modifier = Modifier.weight(1f),
                placeholder = stringResource(Res.string.detail_recommend_textfield_placeholder),
                value = message,
                onValueChange = { message = it },
            )

            OnTrackIconButton(
                modifier = Modifier.size(56.dp),
                icon = Icons.AutoMirrored.Default.Send,
                enabled = isAnyUserSelected,
                onClick = { onSendRecommendation(selectedUserId.orEmpty(), message) },
            )
        }
    }
}

@Composable
fun FriendRecommendSelectorIcon(
    userId: String,
    imageUrl: String?,
    username: String,
    isSelected: Boolean,
    isAnyUserSelected: Boolean,
    onSelectUser: (String?) -> Unit, // Accepts null to deselect
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
                userImageUrl = imageUrl,
                onClick = {
                    if (isSelected) {
                        onSelectUser(null) // Deselect if currently selected
                    } else {
                        onSelectUser(userId) // Select if not selected
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
