package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.features.common.ErrorContent
import de.ashman.ontrack.features.common.LoadingContent
import de.ashman.ontrack.features.detail.components.DetailDropDown
import de.ashman.ontrack.features.detail.components.StickyHeader
import de.ashman.ontrack.features.detail.recommendation.RecommendationViewModel
import de.ashman.ontrack.features.detail.tracking.CurrentSheet
import de.ashman.ontrack.features.detail.tracking.DetailBottomSheet
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_recommendation_added_to_catalog
import ontrack.composeapp.generated.resources.detail_recommendation_passed
import ontrack.composeapp.generated.resources.detail_recommendation_sent
import ontrack.composeapp.generated.resources.tracking_removed
import ontrack.composeapp.generated.resources.tracking_saved
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mediaNavItems: MediaNavigationItems,
    detailViewModel: DetailViewModel,
    recommendationViewModel: RecommendationViewModel,
    onClickItem: (MediaNavigationItems) -> Unit,
    onClickUser: (String) -> Unit,
    onBack: () -> Unit,
) {
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val recommendationUiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentBottomSheet by remember { mutableStateOf(CurrentSheet.TRACK) }
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    LaunchedEffect(mediaNavItems.id) {
        detailViewModel.fetchDetails(mediaNavItems)
        detailViewModel.observeTracking(mediaNavItems.id)
        detailViewModel.observeRatingStats(mediaNavItems.id, mediaNavItems.mediaType)
        detailViewModel.observeFriendTrackings(mediaNavItems.id)
        recommendationViewModel.observeFriendRecommendations(mediaNavItems.id)
        recommendationViewModel.fetchFriends()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = mediaNavItems.mediaType.getMediaTypeUi().outlinedIcon,
                            contentDescription = "Media Type Icon"
                        )
                        Text(
                            text = pluralStringResource(mediaNavItems.mediaType.getMediaTypeUi().title, 1),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back Icon")
                    }
                },
                actions = {
                    DetailDropDown(
                        isTrackingAvailable = detailUiState.selectedTracking != null,
                        onClickRemove = {
                            currentBottomSheet = CurrentSheet.REMOVE
                            showBottomSheet = true
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            StickyHeader(
                imageModifier = Modifier.padding(horizontal = 16.dp),
                media = detailUiState.selectedMedia,
                mediaType = mediaNavItems.mediaType,
                mediaTitle = mediaNavItems.title,
                mediaCoverUrl = mediaNavItems.coverUrl,
                status = detailUiState.selectedTracking?.status,
                scrollBehavior = scrollBehavior,
                onClickAddTracking = {
                    currentBottomSheet = CurrentSheet.TRACK
                    showBottomSheet = true
                },
                onClickRecommend = {
                    currentBottomSheet = CurrentSheet.RECOMMEND
                    showBottomSheet = true
                },
            )

            when (detailUiState.resultState) {
                DetailResultState.Loading -> LoadingContent()

                DetailResultState.Error -> ErrorContent(text = mediaNavItems.mediaType.getMediaTypeUi().error)

                DetailResultState.Success -> {
                    detailUiState.selectedMedia?.let {
                        DetailContent(
                            media = it,
                            appRatingStats = detailUiState.ratingStats,
                            friendTrackings = detailUiState.friendTrackings,
                            friendRecommendations = recommendationUiState.receivedRecommendations,
                            columnListState = listState,
                            onClickItem = onClickItem,
                            onUserClick = onClickUser,
                            onShowFriendActivity = {
                                currentBottomSheet = CurrentSheet.FRIEND_ACTIVITY
                                showBottomSheet = true
                            },
                        )
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                // TODO move when statement out of there and put here.
                DetailBottomSheet(
                    currentContent = currentBottomSheet,
                    user = detailUiState.user,
                    mediaId = mediaNavItems.id,
                    mediaType = mediaNavItems.mediaType,
                    mediaTitle = mediaNavItems.title,
                    mediaCoverUrl = mediaNavItems.coverUrl,
                    tracking = detailUiState.selectedTracking,
                    recommendations = recommendationUiState.receivedRecommendations,
                    previousSentRecommendations = recommendationUiState.previousSentRecommendations,
                    friendTrackings = detailUiState.friendTrackings,
                    friends = recommendationUiState.friends,
                    onSaveTracking = {
                        detailViewModel.saveTracking(it)
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.tracking_saved))
                        }
                    },
                    onRemoveTracking = {
                        detailViewModel.removeTracking(it)
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.tracking_removed))
                        }
                    },
                    onCancel = {
                        showBottomSheet = false
                    },
                    goToReview = {
                        currentBottomSheet = CurrentSheet.REVIEW
                    },
                    onAddToCatalog = {
                        detailViewModel.addRecommendationToCatalog()
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.detail_recommendation_added_to_catalog))
                        }
                    },
                    onPass = {
                        recommendationViewModel.passRecommendation(mediaNavItems.id)
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.detail_recommendation_passed))
                        }
                    },
                    onSendRecommendation = { friendId, message ->
                        recommendationViewModel.sendRecommendation(friendId, message, detailUiState.selectedMedia!!)
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.detail_recommendation_sent))
                        }
                    },
                    selectUser = { recommendationViewModel.selectFriend(it, mediaNavItems.id) },
                    onClickUser = onClickUser,
                )
            }
        }
    }
}
