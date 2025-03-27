package de.ashman.ontrack.features.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.features.feed.comment.CommentsSheet
import de.ashman.ontrack.features.feed.friend.FriendsSheet
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.feed.like.LikesSheet
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_empty
import ontrack.composeapp.generated.resources.feed_nav_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    friendsViewModel: FriendsViewModel,
    sharedUiManager: SharedUiManager,
    onClickCover: (MediaNavigationItems) -> Unit,
    onClickUser: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
) {
    val sharedUiState by sharedUiManager.uiState.collectAsStateWithLifecycle()
    val feedUiState by feedViewModel.uiState.collectAsStateWithLifecycle()
    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()

    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (feedUiState.isFirstLaunch) {
            feedViewModel.fetchInitialTrackingFeed()
        }
    }

    // Refresh when scrolling to bottom
    LaunchedEffect(feedUiState.listState) {
        snapshotFlow { feedUiState.listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && visibleItems.last().index == feedUiState.feedTrackings.lastIndex) {
                    feedViewModel.fetchNextPage()
                }
            }
    }

    // Scroll to top when refreshing
    /* LaunchedEffect(feedUiState.feedTrackings) {
         if (feedUiState.isRefreshing) {
             feedUiState.listState.scrollToItem(0)
         }
     }*/

    LaunchedEffect(sharedUiState.snackbarMessage) {
        sharedUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
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
                title = stringResource(Res.string.feed_nav_title),
                navigationIcon = Icons.Default.Group,
                actionIcon = Icons.Default.Notifications,
                showActionBadge = feedUiState.hasNewNotifications,
                onClickNavigation = { sharedUiManager.showSheet(CurrentSheet.FRIENDS) },
                onClickAction = {
                    feedViewModel.updateHasNewNotifications(false)
                    onNavigateToNotifications()
                },
                scrollBehavior = appBarScrollBehavior,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = feedUiState.feedResultState == FeedResultState.Loading,
            onRefresh = { feedViewModel.fetchInitialTrackingFeed() },
        ) {
            if (feedUiState.feedTrackings.isEmpty()) {
                EmptyFeed()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 80.dp),
                    contentPadding = PaddingValues(16.dp),
                    state = feedUiState.listState,
                ) {
                    items(items = feedUiState.feedTrackings, key = { it.id }) {
                        FeedCard(
                            tracking = it,
                            onLike = { feedViewModel.likeTracking(it) },
                            onShowComments = {
                                feedViewModel.selectTrackingAndShowSheet(
                                    userId = it.userId,
                                    trackingId = it.id,
                                    currentSheet = CurrentSheet.COMMENTS
                                )
                            },
                            onShowLikes = {
                                feedViewModel.selectTrackingAndShowSheet(
                                    userId = it.userId,
                                    trackingId = it.id,
                                    currentSheet = CurrentSheet.LIKES
                                )
                            },
                            onClickCover = onClickCover,
                            onClickUser = { onClickUser(it.userId) },
                        )

                        if (it != feedUiState.feedTrackings.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }

                    if (feedUiState.feedResultState == FeedResultState.LoadingMore) {
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

            if (sharedUiState.showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { sharedUiManager.hideSheet() },
                    sheetState = bottomSheetState,
                    tonalElevation = 0.dp,
                ) {
                    when (sharedUiState.currentSheet) {
                        CurrentSheet.COMMENTS -> CommentsSheet(
                            comments = feedUiState.selectedTracking?.comments ?: emptyList(),
                            onAddComment = feedViewModel::addComment,
                            onRemoveComment = feedViewModel::removeComment,
                            onClickUser = { onClickUser(it) },
                        )

                        CurrentSheet.LIKES -> LikesSheet(
                            likes = feedUiState.selectedTracking?.likes ?: emptyList(),
                            onClickUser = { onClickUser(it) },
                        )

                        CurrentSheet.FRIENDS -> {
                            FriendsSheet(
                                uiState = friendsUiState,
                                isRequestSent = friendsViewModel::isRequestSent,
                                onRemoveFriend = friendsViewModel::removeFriend,
                                onAcceptRequest = friendsViewModel::acceptRequest,
                                onDeclineRequest = friendsViewModel::declineRequest,
                                onCancelRequest = friendsViewModel::cancelRequest,
                                onSendRequest = friendsViewModel::sendRequest,
                                onClickUser = { onClickUser(it) },
                                onQueryChanged = friendsViewModel::onQueryChanged,
                                onSelectFriend = friendsViewModel::selectFriend,
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
                    imageVector = BottomNavItem.FeedNav.filledIcon(),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(Res.string.feed_empty),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
