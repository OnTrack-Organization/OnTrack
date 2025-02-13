package de.ashman.ontrack.features.feed

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.features.feed.comment.CommentsSheetContent
import de.ashman.ontrack.features.feed.like.LikesSheetContent
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_nav_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier,
    viewModel: FeedViewModel,
    onFriendsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClickCover: (MediaNavigationItems) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    var currentSheetContent by remember { mutableStateOf<SheetContent>(SheetContent.Comments) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.feedTrackings) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { index ->
                if (index != null && index >= uiState.feedTrackings.size - 1) {
                    viewModel.fetchTrackingFeed()
                }
            }
    }

    // TODO maybe use BottomSheetScaffold?
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                        onClick = onFriendsClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onLogoutClick,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Logout,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { contentPadding ->
        /*PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = uiState.feedResultState == FeedResultState.Loading,
            onRefresh = { viewModel.fetchTrackingFeed() },
        ) {*/
        LazyColumn(
            // TODO remove the bottom padding and handle nav bar differently
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(bottom = 80.dp),
            contentPadding = PaddingValues(16.dp),
            state = listState,
        ) {
            items(items = uiState.feedTrackings, key = { it.id }) {
                FeedCard(
                    tracking = it,
                    onClickLike = { viewModel.likeTracking(it) },
                    onShowComments = {
                        currentSheetContent = SheetContent.Comments
                        viewModel.selectTracking(it.id)
                        showBottomSheet = true
                    },
                    onClickTrackingHistory = { },
                    onShowLikes = {
                        currentSheetContent = SheetContent.Likes
                        viewModel.selectTracking(it.id)
                        showBottomSheet = true
                    },
                    onClickCover = onClickCover,
                    onUserClick = { onUserClick(it.userId) },
                )

                if (it != uiState.feedTrackings.last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            when (currentSheetContent) {
                SheetContent.Comments -> CommentsSheetContent(
                    comments = uiState.selectedTracking?.comments ?: emptyList(),
                    onAddComment = viewModel::addComment,
                    onDeleteComment = viewModel::deleteComment,
                    onClickUser = {
                        showBottomSheet = false
                        onUserClick(it)
                    },
                )

                SheetContent.Likes -> LikesSheetContent(
                    likes = uiState.selectedTracking?.likes ?: emptyList(),
                    onUserClick = {
                        showBottomSheet = false
                        onUserClick(it)
                    },
                )

                SheetContent.History -> {}
            }
        }
    }
}

enum class SheetContent {
    Comments, Likes, History
}