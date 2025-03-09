package de.ashman.ontrack.features.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.features.feed.comment.CommentsSheet
import de.ashman.ontrack.features.feed.friend.FriendsSheet
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.feed.like.LikesSheet
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationItems
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_empty
import ontrack.composeapp.generated.resources.feed_friend_removed
import ontrack.composeapp.generated.resources.feed_nav_title
import ontrack.composeapp.generated.resources.feed_request_accepted
import ontrack.composeapp.generated.resources.feed_request_cancelled
import ontrack.composeapp.generated.resources.feed_request_declined
import ontrack.composeapp.generated.resources.feed_request_sent
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    friendsViewModel: FriendsViewModel,
    onClickCover: (MediaNavigationItems) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val feedUiState by feedViewModel.uiState.collectAsStateWithLifecycle()
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    var currentSheetContent by remember { mutableStateOf<SheetContent>(SheetContent.COMMENTS) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        feedViewModel.fetchTrackingFeed()
    }

    // TODO add again when using pagination and pull to refresh
    /*LaunchedEffect(feedUiState.feedTrackings) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { index ->
                if (index != null && index >= feedUiState.feedTrackings.size - 1) {
                    feedViewModel.fetchTrackingFeed()
                }
            }
    }*/

    Scaffold(
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 600.dp),
                hostState = snackbarHostState
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.feed_nav_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            currentSheetContent = SheetContent.FRIENDS
                            showBottomSheet = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = if (feedUiState.feedTrackings.isEmpty()) null else appBarScrollBehavior,
            )
        },
    ) { contentPadding ->
        /*PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = uiState.feedResultState == FeedResultState.Loading,
            onRefresh = { viewModel.fetchTrackingFeed() },
        ) {*/
        if (feedUiState.feedTrackings.isEmpty()) {
            EmptyFeed()
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(bottom = 80.dp),
                contentPadding = PaddingValues(16.dp),
                state = listState,
            ) {
                items(items = feedUiState.feedTrackings, key = { it.id }) {
                    FeedCard(
                        tracking = it,
                        onLike = { feedViewModel.likeTracking(it) },
                        onShowComments = {
                            currentSheetContent = SheetContent.COMMENTS
                            feedViewModel.selectTracking(it.id)
                            showBottomSheet = true
                        },
                        onShowLikes = {
                            currentSheetContent = SheetContent.LIKES
                            feedViewModel.selectTracking(it.id)
                            showBottomSheet = true
                        },
                        onClickCover = onClickCover,
                        onClickUser = { onUserClick(it.userId) },
                    )

                    if (it != feedUiState.feedTrackings.last()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                tonalElevation = 0.dp,
            ) {
                when (currentSheetContent) {
                    SheetContent.COMMENTS -> CommentsSheet(
                        comments = feedUiState.selectedTracking?.comments ?: emptyList(),
                        onAddComment = feedViewModel::addComment,
                        onRemoveComment = feedViewModel::removeComment,
                        onClickUser = {
                            showBottomSheet = false
                            onUserClick(it)
                        },
                    )

                    SheetContent.LIKES -> LikesSheet(
                        likes = feedUiState.selectedTracking?.likes ?: emptyList(),
                        onUserClick = {
                            showBottomSheet = false
                            onUserClick(it)
                        },
                    )

                    SheetContent.FRIENDS -> {
                        FriendsSheet(
                            uiState = friendsUiState,
                            onRemoveFriend = {
                                friendsViewModel.removeFriend()

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.feed_friend_removed))
                                }
                            },
                            onAcceptRequest = {
                                friendsViewModel.acceptRequest(it)

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.feed_request_accepted))
                                }
                            },
                            onDeclineRequest = {
                                friendsViewModel.declineRequest(it)

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.feed_request_declined))
                                }
                            },
                            onCancelRequest = {
                                friendsViewModel.cancelRequest(it)

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.feed_request_cancelled))
                                }
                            },
                            onSendRequest = {
                                friendsViewModel.sendRequest(it)

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.feed_request_sent))
                                }
                            },
                            onClickUser = {
                                showBottomSheet = false
                                onUserClick(it)
                            },
                            onQueryChanged = friendsViewModel::onQueryChanged,
                            onSelectFriend = friendsViewModel::selectFriend,
                        )
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
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
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

enum class SheetContent {
    COMMENTS, LIKES, FRIENDS
}