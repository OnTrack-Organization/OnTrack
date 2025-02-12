package de.ashman.ontrack.features.feed.like

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.tracking.TrackingLike
import de.ashman.ontrack.features.common.PersonImage
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_likes
import org.jetbrains.compose.resources.stringResource

@Composable
fun LikesSheetContent(
    likes: List<TrackingLike>,
    onUserClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.feed_likes),
            style = MaterialTheme.typography.titleMedium,
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            items(items = likes, key = { it.userId }) {
                FeedLike(
                    userImageUrl = it.userImageUrl,
                    username = it.username,
                    onClick = { onUserClick(it.userId) },
                )
            }
        }
    }
}

@Composable
fun FeedLike(
    userImageUrl: String,
    username: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PersonImage(
                modifier = Modifier.size(42.dp),
                userImageUrl = userImageUrl,
                onClick = onClick,
            )
            Text(
                text = username,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}