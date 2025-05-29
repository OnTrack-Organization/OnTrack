package de.ashman.ontrack.features.share

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.friend.FriendsSheet
import de.ashman.ontrack.features.friend.FriendsViewModel
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
    commonUiManager: CommonUiManager,
    onClickPost: (String) -> Unit,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onClickNotifications: () -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val postUiState by postViewModel.uiState.collectAsStateWithLifecycle()
    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()

    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

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
                // TODO add later
                showActionBadge = false,
                onClickNavigation = { commonUiManager.showSheet(CurrentSheet.FRIENDS) },
                onClickAction = {
                    //shareViewModel.updateHasNewNotifications(false)
                    onClickNotifications()
                },
                scrollBehavior = appBarScrollBehavior,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = postUiState.resultState == PostResultState.Loading,
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
                            resultState = postUiState.resultState,
                            refreshPosts = { postViewModel.fetchPosts(true) },
                            toggleLike = postViewModel::toggleLike,
                            onShowComments = {
                                commonUiManager.showSheet(CurrentSheet.COMMENTS)
                                postViewModel.fetchComments(it)
                            },
                            onShowLikes = {
                                postViewModel.fetchLikes(it)
                                commonUiManager.showSheet(CurrentSheet.LIKES)
                            },
                            onClickPost = onClickPost,
                            onClickCover = onClickCover,
                            onClickUser = onClickUser,
                        )
                    }

                    PostResultState.Error -> {
                        ShareErrorContent()
                    }

                    PostResultState.Empty -> {
                        ShareEmptyContent()
                    }

                    PostResultState.Loading -> {
                        //TODO()
                    }

                    PostResultState.LoadingMore -> {
                        //TODO()
                    }
                }
            }

            if (commonUiState.showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { commonUiManager.hideSheet() },
                    sheetState = bottomSheetState,
                    tonalElevation = 0.dp,
                ) {
                    when (commonUiState.currentSheet) {
                        CurrentSheet.COMMENTS -> CommentsSheet(
                            comments = postUiState.selectedPost?.comments.orEmpty(),
                            commentCount = postUiState.selectedPost?.commentCount ?: 0,
                            postResultState = postUiState.resultState,
                            onAddComment = postViewModel::addComment,
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
    resultState: PostResultState,
    refreshPosts: () -> Unit,
    toggleLike: (String) -> Unit,
    onShowComments: (String) -> Unit,
    onShowLikes: (String) -> Unit,
    onClickPost: (String) -> Unit,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    // TODO fix
    // Refresh when scrolling to bottom
    /*LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && visibleItems.last().index == posts.lastIndex) {
                    refreshPosts()
                }
            }
    }*/

    LazyColumn(
        modifier = Modifier
            .padding(bottom = 80.dp),
        state = listState,
    ) {
        items(items = posts, key = { it.id }) {
            PostCard(
                post = it,
                onLike = { toggleLike(it.id) },
                onShowComments = { onShowComments(it.id) },
                onShowLikes = { onShowLikes(it.id) },
                onClickCard = { onClickPost(it.id) },
                onClickCover = onClickCover,
                onClickUser = { onClickUser(it.user.id) },
            )

            if (it != posts.last()) {
                HorizontalDivider()
            }
        }

        if (resultState == PostResultState.LoadingMore) {
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
