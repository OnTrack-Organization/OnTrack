package de.ashman.ontrack.features.feed

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import de.ashman.ontrack.authentication.AuthViewModel
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_nav_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier,
    viewModel: FeedViewModel,
    authViewModel: AuthViewModel,
    onFriendsClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCommentsSheet by remember { mutableStateOf(false) }

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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                            contentDescription = "Friends",
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            authViewModel.signOut()
                            onLogoutClick()
                        },
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
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            isRefreshing = uiState.feedResultState == FeedResultState.Loading,
            onRefresh = { viewModel.fetchTrackingFeed() },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                state = listState,
            ) {
                items(items = uiState.feedTrackings, key = { it.id }) {
                    FeedCard(
                        tracking = it,
                        onClickLike = { viewModel.likeTracking(it) },
                        onShowComments = {
                            viewModel.selectTracking(it.id)
                            showCommentsSheet = true
                        },
                        onClickTrackingHistory = { },
                    )
                }
            }
        }

        if (showCommentsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsSheet = false },
                sheetState = bottomSheetState,
            ) {
                uiState.selectedTracking?.let {
                    CommentsContent(
                        comments = it.comments,
                        onAddComment = viewModel::addComment,
                        onDeleteComment = viewModel::deleteComment,
                    )
                }
            }
        }
    }
}