package de.ashman.ontrack.features.share.like

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.features.common.PersonImage
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_likes
import ontrack.composeapp.generated.resources.share_likes_empty
import org.jetbrains.compose.resources.stringResource

@Composable
fun LikesSheet(
    likes: List<Like>,
    onClickUser: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.share_likes, likes.size, likes.size),
            style = MaterialTheme.typography.titleMedium,
        )

        AnimatedContent(
            targetState = likes.isNotEmpty(),
            label = "Likes List Animation"
        ) { hasLikes ->
            LazyColumn(
                state = listState,
            ) {
                if (!hasLikes) {
                    item {
                        Text(
                            text = stringResource(Res.string.share_likes_empty),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(items = likes, key = { it.userId }) {
                        LikeCard(
                            userImageUrl = it.userImageUrl,
                            name = it.name,
                            onClick = { onClickUser(it.userId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LikeCard(
    userImageUrl: String,
    name: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PersonImage(
            profilePictureUrl = userImageUrl,
            onClick = onClick,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
