package de.ashman.ontrack.features.detail.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.recommendation.FriendsActivity
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.getIcon
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.friends_activity_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsActivityRow(
    friendsActivity: FriendsActivity,
    onClickUser: (String) -> Unit,
    onClickMore: () -> Unit,
) {
    val uniqueUsers = (friendsActivity.trackings.map { it.user } + friendsActivity.recommendations.map { it.user }).distinctBy { it.id }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.friends_activity_title),
            style = MaterialTheme.typography.titleMedium,
        )

        IconButton(
            onClick = onClickMore,
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
        items(uniqueUsers) { user ->
            val userTracking = friendsActivity.trackings.find { it.user.id == user.id }
            val userRecommendation = friendsActivity.recommendations.find { it.user.id == user.id }

            FriendsActivityIcon(
                profilePictureUrl = user.profilePictureUrl,
                username = user.username,
                status = userTracking?.status,
                hasRecommendation = userRecommendation != null,
                onClickUser = { onClickUser(user.id) },
            )
        }
    }
}

@Composable
fun FriendsActivityIcon(
    profilePictureUrl: String?,
    username: String,
    status: TrackStatus?,
    hasRecommendation: Boolean,
    onClickUser: () -> Unit,
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier,
        ) {
            PersonImage(
                modifier = Modifier.align(Alignment.TopCenter),
                profilePictureUrl = profilePictureUrl,
                onClick = onClickUser,
            )

            if (hasRecommendation) {
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-8).dp, y = (8).dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = "Recommended Icon",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            status?.let {
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (8).dp, y = (8).dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = it.getIcon(true),
                        contentDescription = "Track Status Icon",
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
            modifier = Modifier.width(64.dp),
            textAlign = TextAlign.Center,
        )
    }
}