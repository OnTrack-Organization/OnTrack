package de.ashman.ontrack.features.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.friend.FriendsSheet
import de.ashman.ontrack.features.friend.FriendsViewModel
import de.ashman.ontrack.features.share.comment.CommentsSheet
import de.ashman.ontrack.features.share.like.LikesSheet
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_empty
import ontrack.composeapp.generated.resources.share_nav_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    shareViewModel: ShareViewModel,
    friendsViewModel: FriendsViewModel,
    commonUiManager: CommonUiManager,
    onClickShareCard: (String) -> Unit,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onClickNotifications: () -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val shareUiState by shareViewModel.uiState.collectAsStateWithLifecycle()
    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()

    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (shareUiState.isFirstLaunch) {
            shareViewModel.fetchInitial()
        }
    }

    // Refresh when scrolling to bottom
    LaunchedEffect(shareUiState.listState) {
        snapshotFlow { shareUiState.listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && visibleItems.last().index == shareUiState.trackings.lastIndex) {
                    shareViewModel.fetchNextPage()
                }
            }
    }

    // Scroll to top when refreshing
    /* LaunchedEffect(feedUiState.feedTrackings) {
         if (feedUiState.isRefreshing) {
             feedUiState.listState.scrollToItem(0)
         }
     }*/

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 600.dp),
                hostState = snackbarHostState
            )
        },
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.share_nav_title),
                navigationIcon = Icons.Default.Group,
                actionIcon = Icons.Default.Notifications,
                showActionBadge = shareUiState.hasNewNotifications,
                onClickNavigation = { commonUiManager.showSheet(CurrentSheet.FRIENDS) },
                onClickAction = {
                    shareViewModel.updateHasNewNotifications(false)
                    onClickNotifications()
                },
                scrollBehavior = appBarScrollBehavior,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = shareUiState.shareResultState == ShareResultState.Loading,
            onRefresh = { shareViewModel.fetchInitial() },
        ) {
            if (shareUiState.trackings.isEmpty()) {
                EmptyFeed()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 80.dp),
                    //contentPadding = PaddingValues(16.dp),
                    state = shareUiState.listState,
                ) {
                    items(items = shareUiState.trackings, key = { it.id }) {
                        ShareCard(
                            tracking = it,
                            onLike = { shareViewModel.likeTracking(it) },
                            onShowComments = {
                                shareViewModel.selectTrackingAndShowSheet(
                                    userId = it.userId,
                                    trackingId = it.id,
                                    currentSheet = CurrentSheet.COMMENTS
                                )
                            },
                            onShowLikes = {
                                shareViewModel.selectTrackingAndShowSheet(
                                    userId = it.userId,
                                    trackingId = it.id,
                                    currentSheet = CurrentSheet.LIKES
                                )
                            },
                            onClickCard = { onClickShareCard(it.id) },
                            onClickCover = onClickCover,
                            onClickUser = { onClickUser(it.userId) },
                        )

                        if (it != shareUiState.trackings.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }

                    if (shareUiState.shareResultState == ShareResultState.LoadingMore) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            if (commonUiState.showSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        commonUiManager.hideSheet()
                        friendsViewModel.clearQuery()
                    },
                    sheetState = bottomSheetState,
                    tonalElevation = 0.dp,
                ) {
                    when (commonUiState.currentSheet) {
                        CurrentSheet.COMMENTS -> CommentsSheet(
                            comments = shareUiState.selectedTracking?.comments ?: emptyList(),
                            onAddComment = shareViewModel::addComment,
                            onRemoveComment = shareViewModel::removeComment,
                            onClickUser = { onClickUser(it) },
                        )

                        CurrentSheet.LIKES -> LikesSheet(
                            likes = shareUiState.selectedTracking?.likes ?: emptyList(),
                            onClickUser = { onClickUser(it) },
                        )

                        CurrentSheet.FRIENDS -> {
                            FriendsSheet(
                                uiState = friendsUiState,
                                fetchFriendsAndRequests = friendsViewModel::fetchFriendsAndRequests,
                                onSendRequest = friendsViewModel::sendRequest,
                                onCancelRequest = friendsViewModel::cancelRequest,
                                onAcceptRequest = friendsViewModel::acceptRequest,
                                onDeclineRequest = friendsViewModel::declineRequest,
                                onRemoveFriend = friendsViewModel::removeFriend,
                                onClickUser = { onClickUser(it) },
                                onQueryChanged = friendsViewModel::onQueryChanged,
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFeed(
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
                    imageVector = BottomNavItem.ShareNav.filledIcon(),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(Res.string.share_empty),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
