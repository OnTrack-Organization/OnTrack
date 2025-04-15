package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.newdomains.NewUser
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.LargerImageDialog
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelf_nav_title
import ontrack.composeapp.generated.resources.shelf_own_empty
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(
    viewModel: ShelfViewModel,
    commonUiManager: CommonUiManager,
    userId: String,
    emptyText: StringResource = Res.string.shelf_own_empty,
    onClickMoreMedia: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
    onSettings: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()

    var showImageDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            Column {
                OnTrackTopBar(
                    title = stringResource(Res.string.shelf_nav_title),
                    navigationIcon = onBack?.let { Icons.AutoMirrored.Default.ArrowBack },
                    onClickNavigation = { onBack?.invoke() },
                    actionIcon = onSettings?.let { Icons.Default.Settings },
                    onClickAction = { onSettings?.invoke() },
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            uiState.user?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        UserHeader(
                            modifier = Modifier.weight(1f),
                            name = user.name,
                            username = user.username,
                            imageUrl = user.profilePictureUrl,
                            showLargeImage = { showImageDialog = true },
                        )

                        if (viewModel.isOtherUser()) {
                            FriendRequestButton(
                                friendRequestStatus = uiState.friendRequestStatus,
                                onSendRequest = viewModel::sendRequest,
                                onCancelRequest = viewModel::cancelRequest,
                                onRemoveFriend = viewModel::removeFriend,
                                onAcceptRequest = viewModel::acceptRequest,
                                onDeclineRequest = viewModel::declineRequest,
                            )
                        }
                    }

                    HorizontalDivider()
                }
            }

            if (uiState.trackings.isEmpty()) {
                EmptyShelf(text = emptyText)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (onSettings != null) 80.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    state = viewModel.listState,
                ) {
                    item {
                        MediaCounts(trackings = uiState.trackings)
                    }

                    MediaType.entries.forEach { mediaType ->
                        val filteredTrackings = uiState.trackings.filter { it.mediaType == mediaType }

                        if (filteredTrackings.isNotEmpty()) {
                            item(key = mediaType.name) {
                                ShelfItem(
                                    mediaType = mediaType,
                                    items = filteredTrackings,
                                    onClickMore = { onClickMoreMedia(mediaType) },
                                    onClickItem = onClickItem,
                                )
                            }
                        }
                    }
                }
            }
        }

        LargerImageDialog(
            showDialog = showImageDialog,
            imageUrl = uiState.user?.profilePictureUrl,
            onDismiss = { showImageDialog = false },
        )
    }
}

@Composable
fun UserHeader(
    modifier: Modifier = Modifier,
    name: String,
    username: String,
    imageUrl: String?,
    showLargeImage: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PersonImage(
            userImageUrl = imageUrl,
            size = 56.dp,
            onClick = showLargeImage,
        )

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun MediaCounts(
    trackings: List<Tracking>,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaType.entries.forEach { mediaType ->
                val count = trackings.count { it.mediaType == mediaType }

                MediaCount(
                    icon = mediaType.getMediaTypeUi().outlinedIcon,
                    count = count
                )
            }
        }
    }
}

@Composable
fun MediaCount(
    icon: ImageVector,
    count: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                imageVector = icon,
                contentDescription = "Media Icon",
            )
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ShelfItem(
    mediaType: MediaType,
    items: List<Tracking>?,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    items?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.size(42.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            imageVector = mediaType.getMediaTypeUi().outlinedIcon,
                            contentDescription = "Media Icon",
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = pluralStringResource(mediaType.getMediaTypeUi().title, 2),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                IconButton(
                    onClick = { onClickMore(mediaType) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Show more",
                    )
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(items.take(5)) { item ->
                    MediaPoster(
                        modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                        coverUrl = item.mediaCoverUrl,
                        trackStatusIcon = item.status?.getIcon(true),
                        onClick = { onClickItem(MediaNavigationItems(item.mediaId, item.mediaTitle, item.mediaCoverUrl, item.mediaType)) },
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyShelf(
    text: StringResource,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp)
            .padding(bottom = 145.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = BottomNavItem.ShelfNav.filledIcon(),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

fun NewUser.toFriendRequest() = FriendRequest(
    userId = id,
    username = username,
    name = name,
    imageUrl = profilePictureUrl,
)

fun NewUser.toFriend() = Friend(
    id = id,
    username = username,
    imageUrl = profilePictureUrl,
    name = name,
)