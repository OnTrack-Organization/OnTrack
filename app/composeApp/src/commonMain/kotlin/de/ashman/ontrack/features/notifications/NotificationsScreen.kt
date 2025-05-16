package de.ashman.ontrack.features.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.MINI_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_empty
import ontrack.composeapp.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onBack: () -> Unit,
    onNotificationClick: (String) -> Unit,
    onClickUser: (String) -> Unit,
    onClickMedia: (MediaNavigationParam) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.notifications_title),
                titleIcon = Icons.Default.Notifications,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                onClickNavigation = onBack,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = uiState.resultState == NotificationsResultState.Loading,
            onRefresh = { },
        ) {
            if (uiState.recentNotifications.isEmpty()) EmptyNotifications()

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                items(uiState.recentNotifications) {
                    NotificationCard(
                        notification = it,
                        onNotificationClick = onNotificationClick,
                        onUserClick = onClickUser,
                        onMediaClick = {
                            onClickMedia(
                                MediaNavigationParam(
                                    id = it.mediaId,
                                    title = it.mediaTitle,
                                    coverUrl = it.mediaImageUrl,
                                    mediaType = it.mediaType,
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationData,
    onNotificationClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onMediaClick: () -> Unit,
) {
    val notificationDataUi = notification.type.getUI()

    Row(
        modifier = Modifier.clickable(onClick = { onNotificationClick(notification.trackingId) }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        NotificationUserImage(
            userId = notification.senderUserId,
            imageUrl = notification.senderUserImageUrl,
            icon = notificationDataUi.icon,
            onUserClick = onUserClick
        )

        NotificationTexts(
            modifier = Modifier.weight(1f),
            notificationMessage = notificationDataUi.message(notification),
            timestamp = notification.timestamp
        )

        MediaPoster(
            modifier = Modifier.height(MINI_POSTER_HEIGHT),
            coverUrl = notification.mediaImageUrl,
            onClick = onMediaClick,
        )
    }
}

@Composable
fun NotificationUserImage(
    userId: String,
    imageUrl: String?,
    icon: ImageVector,
    onUserClick: (String) -> Unit,
) {
    Box {
        PersonImage(
            modifier = Modifier.align(Alignment.TopStart),
            profilePictureUrl = imageUrl,
            onClick = { onUserClick(userId) }
        )

        Surface(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (8).dp, y = (8).dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun NotificationTexts(
    modifier: Modifier = Modifier,
    notificationMessage: AnnotatedString,
    timestamp: Long,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = notificationMessage,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = timestamp.formatTimeAgoString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun EmptyNotifications(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.NotificationsOff,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(Res.string.notifications_empty),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}