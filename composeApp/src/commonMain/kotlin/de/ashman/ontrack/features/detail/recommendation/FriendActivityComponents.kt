package de.ashman.ontrack.features.detail.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.getIcon
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_friends_activity_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendActivityRow(
    friendTrackings: List<Tracking>,
    friendRecommendations: List<Recommendation>,
    onUserClick: (String) -> Unit,
    onMoreClick: () -> Unit,
) {
    val uniqueFriends = (friendTrackings.map { it.userId } + friendRecommendations.map { it.userId }).distinct()

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.detail_friends_activity_title),
            style = MaterialTheme.typography.titleMedium,
        )

        IconButton(
            onClick = onMoreClick,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Show more",
            )
        }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(uniqueFriends) { userId ->
            val userTracking = friendTrackings.find { it.userId == userId }
            val userRecommendation = friendRecommendations.find { it.userId == userId }

            val userImageUrl = userTracking?.userImageUrl ?: userRecommendation?.userImageUrl
            val status = userTracking?.status
            val isRecommended = userRecommendation != null

            FriendActivityIcon(
                userId = userId,
                imageUrl = userImageUrl,
                status = status,
                isRecommended = isRecommended,
                onUserClick = onUserClick
            )
        }
    }
}

@Composable
fun FriendActivityIcon(
    userId: String,
    imageUrl: String?,
    status: TrackStatus?,
    isRecommended: Boolean,
    onUserClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier.size(64.dp),
    ) {
        PersonImage(
            modifier = Modifier.align(Alignment.Center),
            userImageUrl = imageUrl,
            onClick = { onUserClick(userId) }
        )
        status?.let {
            Surface(
                modifier = Modifier.size(24.dp).align(Alignment.BottomEnd),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = it.getIcon(true),
                    contentDescription = "Trackstatus Icon",
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }

        if (isRecommended) {
            Surface(
                modifier = Modifier.size(24.dp).align(Alignment.BottomStart),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = "Recommended Icon",
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }
    }
}