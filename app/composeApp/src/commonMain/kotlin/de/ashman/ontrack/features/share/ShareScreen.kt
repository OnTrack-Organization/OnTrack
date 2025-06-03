package de.ashman.ontrack.features.share

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WifiOff
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.friend.FriendsSheet
import de.ashman.ontrack.features.friend.FriendsViewModel
import de.ashman.ontrack.features.notification.NotificationViewModel
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_empty
import ontrack.composeapp.generated.resources.share_error
import ontrack.composeapp.generated.resources.share_nav_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    postViewModel: PostViewModel,
    friendsViewModel: FriendsViewModel,
    notificationViewModel: NotificationViewModel,
    commonUiManager: CommonUiManager,
    onClickPost: (String) -> Unit,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onClickNotifications: () -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val postUiState by postViewModel.uiState.collectAsStateWithLifecycle()
    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()
    val notificationUiState by notificationViewModel.uiState.collectAsStateWithLifecycle()

    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        //modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
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
                showActionBadge = notificationUiState.hasUnreadNotifications,
                onClickNavigation = { commonUiManager.showSheet(CurrentSheet.FRIENDS) },
                onClickAction = onClickNotifications,
                scrollBehavior = appBarScrollBehavior,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = postUiState.loading,
            onRefresh = { postViewModel.fetchPosts(true) },
        ) {
            AnimatedContent(
                targetState = postUiState.resultState,
                label = "ResultStateAnimation"
            ) { state ->
                when (state) {
                    PostResultState.Success -> {
                        ShareSuccessContent(
                            posts = postUiState.posts,
                            loadingMore = postUiState.loadingMore,
                            toggleLike = postViewModel::toggleLike,
                            onShowComments = {
                                postViewModel.setSelectedPost(it)
                                postViewModel.fetchComments(it.id, true)
                                commonUiManager.showSheet(CurrentSheet.COMMENTS)
                            },
                            onShowLikes = {
                                postViewModel.setSelectedPost(it)
                                postViewModel.fetchLikes(it.id, true)
                                commonUiManager.showSheet(CurrentSheet.LIKES)
                            },
                            onClickPost = onClickPost,
                            onClickCover = onClickCover,
                            onClickUser = onClickUser,
                            fetchNextPage = { postViewModel.fetchPosts(false) },
                        )
                    }

                    PostResultState.Error -> {
                        ShareErrorContent()
                    }

                    PostResultState.Empty -> {
                        ShareEmptyContent()
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
                            comments = postUiState.selectedPost?.comments.orEmpty(),
                            commentCount = postUiState.selectedPost?.commentCount ?: 0,
                            postResultState = postUiState.resultState,
                            onFetchNextPage = { postViewModel.fetchComments(postUiState.selectedPost?.id.orEmpty(), true) },
                            onPostComment = postViewModel::addComment,
                            onRemoveComment = postViewModel::removeComment,
                            onClickUser = { onClickUser(it) },
                        )

                        CurrentSheet.LIKES -> LikesSheet(
                            likes = postUiState.selectedPost?.likes.orEmpty(),
                            likeCount = postUiState.selectedPost?.likeCount ?: 0,
                            postResultState = postUiState.resultState,
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
fun ShareSuccessContent(
    posts: List<Post>,
    loadingMore: Boolean,
    toggleLike: (String) -> Unit,
    onShowComments: (Post) -> Unit,
    onShowLikes: (Post) -> Unit,
    onClickPost: (String) -> Unit,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    fetchNextPage: () -> Unit,
) {
    val listState = rememberLazyListState()

    InfiniteListHandler(
        listState = listState,
        onLoadMore = fetchNextPage
    )

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        items(items = posts, key = { it.id }) {
            PostCard(
                post = it,
                onLike = { toggleLike(it.id) },
                onShowComments = { onShowComments(it) },
                onShowLikes = { onShowLikes(it) },
                onClickCard = { onClickPost(it.id) },
                onClickCover = onClickCover,
                onClickUser = { onClickUser(it.user.id) },
            )

            if (it != posts.last()) {
                HorizontalDivider()
            }
        }

        if (loadingMore) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ShareEmptyContent(
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

@Composable
fun ShareErrorContent(
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
                    text = stringResource(Res.string.share_error),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = 1,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleIndex >= totalItemsCount - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect { loadMore ->
                if (loadMore) {
                    onLoadMore()
                }
            }
    }
}
