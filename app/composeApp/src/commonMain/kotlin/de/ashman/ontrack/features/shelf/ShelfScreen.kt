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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.ConfirmSheet
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.LargerImageDialog
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.report.ReportSheet
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.block_button
import ontrack.composeapp.generated.resources.block_confirm_text
import ontrack.composeapp.generated.resources.block_confirm_title
import ontrack.composeapp.generated.resources.report_button
import ontrack.composeapp.generated.resources.shelf_nav_title
import ontrack.composeapp.generated.resources.shelf_own_empty
import ontrack.composeapp.generated.resources.unblock_button
import ontrack.composeapp.generated.resources.unblock_confirm_text
import ontrack.composeapp.generated.resources.unblock_confirm_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(
    viewModel: ShelfViewModel,
    commonUiManager: CommonUiManager,
    userId: String? = null,
    emptyText: StringResource = Res.string.shelf_own_empty,
    onClickMoreMedia: (MediaType) -> Unit,
    onClickItem: (MediaNavigationParam) -> Unit,
    onSettings: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val shelfUiState by viewModel.uiState.collectAsState()

    var showImageDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.shelf_nav_title),
                navigationIcon = onBack?.let { Icons.AutoMirrored.Default.ArrowBack },
                onClickNavigation = { onBack?.invoke() },
                customActions = {
                    if (userId == null) {
                        onSettings?.let {
                            IconButton(onClick = it) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        ShelfDropDownMenu(
                            isBlocked = shelfUiState.isBlocked,
                            onClickBlock = { commonUiManager.showSheet(CurrentSheet.BLOCK) },
                            onClickUnblock = { commonUiManager.showSheet(CurrentSheet.UNBLOCK) },
                            onClickReport = { commonUiManager.showSheet(CurrentSheet.REPORT) },
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            shelfUiState.user?.let {
                ShelfHeader(
                    user = it,
                    friendStatus = shelfUiState.friendStatus,
                    isBlocked = shelfUiState.isBlocked,
                    sendRequest = viewModel::sendRequest,
                    cancelRequest = viewModel::cancelRequest,
                    acceptRequest = viewModel::acceptRequest,
                    declineRequest = viewModel::declineRequest,
                    removeFriend = viewModel::removeFriend,
                    showLargeImage = { showImageDialog = true },
                )
            }

            if (shelfUiState.trackings.isEmpty()) {
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
                        MediaCounts(trackings = shelfUiState.trackings)
                    }

                    MediaType.entries.forEach { mediaType ->
                        val filteredTrackings = shelfUiState.trackings.filter { it.media.type == mediaType }

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
            imageUrl = shelfUiState.user?.profilePictureUrl,
            onDismiss = { showImageDialog = false },
        )

        if (commonUiState.showSheet) {
            ModalBottomSheet(
                modifier = Modifier.imePadding(),
                onDismissRequest = commonUiManager::hideSheet,
                sheetState = sheetState,
            ) {
                when (commonUiState.currentSheet) {
                    CurrentSheet.BLOCK -> {
                        ConfirmSheet(
                            title = Res.string.block_confirm_title,
                            text = Res.string.block_confirm_text,
                            isLoading = shelfUiState.isLoading,
                            onConfirm = viewModel::blockUser,
                            onCancel = commonUiManager::hideSheet,
                        )
                    }

                    CurrentSheet.UNBLOCK -> {
                        ConfirmSheet(
                            title = Res.string.unblock_confirm_title,
                            text = Res.string.unblock_confirm_text,
                            isLoading = shelfUiState.isLoading,
                            onConfirm = viewModel::unblockUser,
                            onCancel = commonUiManager::hideSheet,
                        )
                    }

                    CurrentSheet.REPORT -> {
                        ReportSheet(
                            isLoading = shelfUiState.isLoading,
                            onReport = viewModel::reportUser,
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun ShelfHeader(
    user: User,
    friendStatus: FriendStatus?,
    isBlocked: Boolean = false,
    sendRequest: () -> Unit = {},
    cancelRequest: () -> Unit = {},
    acceptRequest: () -> Unit = {},
    declineRequest: () -> Unit = {},
    removeFriend: () -> Unit = {},
    showLargeImage: () -> Unit = {},
) {
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
                showLargeImage = showLargeImage,
            )

            friendStatus?.let {
                FriendRequestButton(
                    friendStatus = friendStatus,
                    isBlocked = isBlocked,
                    onSendRequest = sendRequest,
                    onCancelRequest = cancelRequest,
                    onAcceptRequest = acceptRequest,
                    onDeclineRequest = declineRequest,
                    onRemoveFriend = removeFriend,
                )
            }
        }

        HorizontalDivider()
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
            profilePictureUrl = imageUrl,
            size = 56.dp,
            onClick = showLargeImage,
        )

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = name,
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
                val count = trackings.count { it.media.type == mediaType }

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
    onClickItem: (MediaNavigationParam) -> Unit,
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
                            imageVector = mediaType.getMediaTypeUi().icon,
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
                        coverUrl = item.media.coverUrl,
                        trackStatusIcon = item.status.getIcon(true),
                        onClick = {
                            onClickItem(
                                MediaNavigationParam(
                                    id = item.media.id,
                                    title = item.media.title,
                                    coverUrl = item.media.coverUrl,
                                    type = item.media.type,
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
fun EmptyShelf(
    text: StringResource,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
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

@Composable
fun ShelfDropDownMenu(
    onClickBlock: () -> Unit,
    onClickUnblock: () -> Unit,
    onClickReport: () -> Unit,
    isBlocked: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(if (isBlocked) Res.string.unblock_button else Res.string.block_button)) },
                leadingIcon = {
                    Icon(Icons.Default.Block, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    if (isBlocked) onClickUnblock() else onClickBlock()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.report_button)) },
                leadingIcon = {
                    Icon(Icons.Default.Report, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    onClickReport()
                }
            )
        }
    }
}