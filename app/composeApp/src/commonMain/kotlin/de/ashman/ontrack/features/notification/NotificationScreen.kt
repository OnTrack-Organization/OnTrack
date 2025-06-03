package de.ashman.ontrack.features.notification

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.media.MediaData
import de.ashman.ontrack.domain.notification.FriendRequestAccepted
import de.ashman.ontrack.domain.notification.FriendRequestReceived
import de.ashman.ontrack.domain.notification.PostMentioned
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.domain.notification.PostCommented
import de.ashman.ontrack.domain.notification.PostLiked
import de.ashman.ontrack.domain.notification.RecommendationReceived
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.MINI_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_empty
import ontrack.composeapp.generated.resources.notifications_error
import ontrack.composeapp.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    commonUiManager: CommonUiManager,
    onBack: () -> Unit,
    onClickPost: (String) -> Unit,
    onClickUser: (String) -> Unit,
    onClickMedia: (MediaNavigationParam) -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val notificationUiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.notifications_title),
                titleIcon = Icons.Default.Notifications,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                actionIcon = Icons.Default.Drafts,
                onClickAction = viewModel::markAllAsRead,
                onClickNavigation = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = notificationUiState.resultState == NotificationResultState.Loading,
            onRefresh = { viewModel.loadNotifications() },
        ) {
            AnimatedContent(
                targetState = notificationUiState.resultState,
                label = "ResultStateAnimation"
            ) { state ->
                when (state) {
                    NotificationResultState.Success -> {
                        NotificationSuccess(
                            notifications = notificationUiState.notifications,
                            onMarkAsRead = viewModel::markAsRead,
                            onClickPost = onClickPost,
                            onClickUser = onClickUser,
                            onClickMedia = onClickMedia,
                        )
                    }

                    NotificationResultState.Loading -> Unit
                    NotificationResultState.Error -> NotificationError()
                    NotificationResultState.Empty -> NotificationEmpty()
                }
            }
        }
    }
}

@Composable
fun NotificationSuccess(
    notifications: List<Notification>,
    onMarkAsRead: (String) -> Unit,
    onClickPost: (String) -> Unit,
    onClickUser: (String) -> Unit,
    onClickMedia: (MediaNavigationParam) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        items(notifications) {
            NotificationCard(
                notification = it,
                onMarkAsRead = onMarkAsRead,
                onClickUser = onClickUser,
                onClickPost = onClickPost,
                onClickMedia = {
                    onClickMedia(
                        MediaNavigationParam(
                            id = it.id,
                            title = it.title,
                            coverUrl = it.coverUrl,
                            type = it.type,
                        )
                    )
                },
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    onMarkAsRead: (String) -> Unit,
    onClickPost: (String) -> Unit,
    onClickUser: (String) -> Unit,
    onClickMedia: (MediaData) -> Unit,
) {
    val ui = notification.getUiType()

    Box(
        modifier = Modifier
            .clickable {
                onMarkAsRead(notification.id)

                when (notification) {
                    // TODO maybe open friendssheet instead
                    is FriendRequestReceived, is FriendRequestAccepted -> onClickUser(notification.sender.id)
                    is RecommendationReceived -> onClickMedia(notification.recommendation.media)
                    is PostLiked -> onClickPost(notification.post.id)
                    is PostCommented -> onClickPost(notification.post.id)
                    is PostMentioned -> onClickPost(notification.post.id)
                }
            }
            .background(if (notification.read) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NotificationUserImage(
                user = notification.sender,
                icon = ui.icon,
                onClick = onClickUser,
            )

            NotificationTexts(
                modifier = Modifier.weight(1f),
                notificationMessage = ui.message(notification),
                timestamp = notification.createdAt,
            )

            when (notification) {
                is PostLiked -> {
                    MediaPoster(
                        modifier = Modifier.height(MINI_POSTER_HEIGHT),
                        coverUrl = notification.post.tracking.media.coverUrl,
                        onClick = { onClickMedia(notification.post.tracking.media) }
                    )
                }

                is PostCommented -> {
                    MediaPoster(
                        modifier = Modifier.height(MINI_POSTER_HEIGHT),
                        coverUrl = notification.post.tracking.media.coverUrl,
                        onClick = { onClickMedia(notification.post.tracking.media) }
                    )
                }

                is RecommendationReceived -> {
                    MediaPoster(
                        modifier = Modifier.height(MINI_POSTER_HEIGHT),
                        coverUrl = notification.recommendation.media.coverUrl,
                        onClick = { onClickMedia(notification.recommendation.media) }
                    )
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun NotificationEmpty(
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

@Composable
fun NotificationError(
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
                    imageVector = Icons.Default.WifiOff,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(Res.string.notifications_error),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun NotificationUserImage(
    user: User,
    icon: ImageVector,
    onClick: (String) -> Unit,
) {
    Box {
        PersonImage(
            modifier = Modifier.align(Alignment.TopStart),
            profilePictureUrl = user.profilePictureUrl,
            onClick = { onClick(user.id) }
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
