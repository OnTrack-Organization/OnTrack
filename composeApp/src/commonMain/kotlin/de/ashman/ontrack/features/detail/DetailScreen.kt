package de.ashman.ontrack.features.detail

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.api.getRatingType
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.media.Album
import de.ashman.ontrack.domain.media.Boardgame
import de.ashman.ontrack.domain.media.Book
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.media.Movie
import de.ashman.ontrack.domain.media.Show
import de.ashman.ontrack.domain.media.Videogame
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.ErrorContent
import de.ashman.ontrack.features.common.LoadingContent
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.RemoveSheet
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.features.detail.components.DetailDropDown
import de.ashman.ontrack.features.detail.components.RatingCardRow
import de.ashman.ontrack.features.detail.components.StickyHeader
import de.ashman.ontrack.features.detail.media.AlbumDetailContent
import de.ashman.ontrack.features.detail.media.BoardgameDetailContent
import de.ashman.ontrack.features.detail.media.BookDetailContent
import de.ashman.ontrack.features.detail.media.MovieDetailContent
import de.ashman.ontrack.features.detail.media.ShowDetailContent
import de.ashman.ontrack.features.detail.media.VideogameDetailContent
import de.ashman.ontrack.features.detail.recommendation.FriendActivityRow
import de.ashman.ontrack.features.detail.recommendation.FriendsActivitySheet
import de.ashman.ontrack.features.detail.recommendation.RecommendSheet
import de.ashman.ontrack.features.detail.recommendation.RecommendationViewModel
import de.ashman.ontrack.features.detail.tracking.ReviewSheet
import de.ashman.ontrack.features.detail.tracking.TrackSheet
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import kotlinx.datetime.Clock.System
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_remove_confirm_text
import ontrack.composeapp.generated.resources.detail_remove_confirm_title
import org.jetbrains.compose.resources.pluralStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mediaNavItems: MediaNavigationItems,
    sharedUiManager: SharedUiManager,
    detailViewModel: DetailViewModel,
    recommendationViewModel: RecommendationViewModel,
    onClickItem: (MediaNavigationItems) -> Unit,
    onClickUser: (String) -> Unit,
    onBack: () -> Unit,
) {
    val sharedUiState by sharedUiManager.uiState.collectAsStateWithLifecycle()
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val recommendationUiState by recommendationViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    LaunchedEffect(mediaNavItems.id) {
        detailViewModel.fetchDetails(mediaNavItems)
        detailViewModel.observeTracking(mediaNavItems.id)
        detailViewModel.observeRatingStats(mediaNavItems.id, mediaNavItems.mediaType)
        detailViewModel.observeFriendTrackings(mediaNavItems.id)
        recommendationViewModel.observeFriendRecommendations(mediaNavItems.id)
        recommendationViewModel.fetchFriends()
    }

    LaunchedEffect(sharedUiState.snackbarMessage) {
        sharedUiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            sharedUiManager.clearSnackbarMessage()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            sharedUiManager.resetUiState()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            OnTrackTopBar(
                title = pluralStringResource(mediaNavItems.mediaType.getMediaTypeUi().title, 1),
                titleIcon = mediaNavItems.mediaType.getMediaTypeUi().outlinedIcon,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                onClickNavigation = onBack,
                dropdownMenu = {
                    DetailDropDown(
                        isTrackingAvailable = detailUiState.selectedTracking != null,
                        onClickRemove = { sharedUiManager.showSheet(CurrentSheet.REMOVE) }
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
                onClickAddTracking = { sharedUiManager.showSheet(CurrentSheet.TRACK) },
                onClickRecommend = { sharedUiManager.showSheet(CurrentSheet.RECOMMEND) },
            )

            when (detailUiState.resultState) {
                DetailResultState.Loading -> LoadingContent()

                DetailResultState.Error -> ErrorContent(text = mediaNavItems.mediaType.getMediaTypeUi().error)

                DetailResultState.Success -> {
                    DetailContent(
                        media = detailUiState.selectedMedia,
                        selectedTracking = detailUiState.selectedTracking,
                        ratingStats = detailUiState.ratingStats,
                        friendTrackings = detailUiState.friendTrackings,
                        receivedRecommendations = recommendationUiState.receivedRecommendations,
                        columnListState = listState,
                        onClickItem = onClickItem,
                        onUserClick = onClickUser,
                        onShowFriendActivity = { sharedUiManager.showSheet(CurrentSheet.FRIEND_ACTIVITY) },
                    )
                }
            }
        }

        if (sharedUiState.showSheet) {
            ModalBottomSheet(
                onDismissRequest = { sharedUiManager.hideSheet() },
                sheetState = sheetState,
            ) {
                val localFocusManager = LocalFocusManager.current
                // Copy previous tracking with new id and timestamp or create new tracking with filled media data
                // TODO move out into vm
                var tracking by remember {
                    mutableStateOf(
                        detailUiState.selectedTracking?.copy(
                            timestamp = System.now().toEpochMilliseconds(),
                        ) ?: Tracking(
                            userId = detailUiState.user?.id.orEmpty(),
                            username = detailUiState.user?.name.orEmpty(),
                            userImageUrl = detailUiState.user?.imageUrl.orEmpty(),
                            mediaId = mediaNavItems.id,
                            mediaType = mediaNavItems.mediaType,
                            mediaTitle = mediaNavItems.title,
                            mediaCoverUrl = mediaNavItems.coverUrl,
                            timestamp = System.now().toEpochMilliseconds(),
                        )
                    )
                }

                // TODO add bottom sheet ui wrapper that holds the title and handles spacing
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                localFocusManager.clearFocus()
                            })
                        },
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (sharedUiState.currentSheet) {
                        CurrentSheet.TRACK -> TrackSheet(
                            mediaType = mediaNavItems.mediaType,
                            selectedStatus = tracking.status,
                            onSelectStatus = { tracking = tracking.copy(status = it) },
                            onSave = {
                                tracking = tracking.copy(
                                    rating = if (tracking.status == TrackStatus.CATALOG) null else tracking.rating,
                                    reviewTitle = if (tracking.status == TrackStatus.CATALOG) null else tracking.reviewTitle,
                                    reviewDescription = if (tracking.status == TrackStatus.CATALOG) null else tracking.reviewDescription,
                                    timestamp = System.now().toEpochMilliseconds()
                                ).also {
                                    detailViewModel.saveTracking(it)
                                }
                            },
                            onToReview = { sharedUiManager.showSheet(CurrentSheet.REVIEW) },
                        )

                        CurrentSheet.REVIEW -> ReviewSheet(
                            reviewTitle = tracking.reviewTitle,
                            reviewDescription = tracking.reviewDescription,
                            rating = tracking.rating,
                            trackStatus = tracking.status,
                            onReviewTitleChange = { tracking = tracking.copy(reviewTitle = it) },
                            onReviewDescriptionChange = { tracking = tracking.copy(reviewDescription = it) },
                            onRatingChange = { tracking = tracking.copy(rating = it) },
                            onSave = { detailViewModel.saveTracking(tracking) },
                        )

                        CurrentSheet.REMOVE -> RemoveSheet(
                            title = Res.string.detail_remove_confirm_title,
                            text = Res.string.detail_remove_confirm_text,
                            onConfirm = { detailViewModel.removeTracking(tracking.id) },
                            onCancel = { sharedUiManager.hideSheet() },
                        )

                        CurrentSheet.FRIEND_ACTIVITY -> FriendsActivitySheet(
                            recommendations = recommendationUiState.receivedRecommendations,
                            friendTrackings = detailUiState.friendTrackings,
                            hasTracking = tracking.status != null,
                            onUserClick = onClickUser,
                            onAddToCatalogClick = detailViewModel::addRecommendationToCatalog,
                            onPassClick = { recommendationViewModel.passRecommendation(mediaNavItems.id) },
                        )

                        CurrentSheet.RECOMMEND -> RecommendSheet(
                            selectableFriends = recommendationUiState.friends,
                            previousSentRecommendations = recommendationUiState.previousSentRecommendations,
                            onSendRecommendation = { friendId, message ->
                                recommendationViewModel.sendRecommendation(friendId, message, detailUiState.selectedMedia!!)
                            },
                            selectUser = { recommendationViewModel.selectFriend(it, mediaNavItems.id) },
                            onClickUser = onClickUser,
                        )

                        else -> {}
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    media: Media?,
    selectedTracking: Tracking?,
    ratingStats: RatingStats,
    friendTrackings: List<Tracking>,
    receivedRecommendations: List<Recommendation>,
    columnListState: LazyListState,
    onClickItem: (MediaNavigationItems) -> Unit,
    onUserClick: (String) -> Unit,
    onShowFriendActivity: () -> Unit,
) {
    media?.let {
        LazyColumn(
            state = columnListState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            /*item {
                selectedTracking?.let {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.detail_current_tracking),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        TrackingCard(
                            userImageUrl = it.userImageUrl,
                            username = it.username,
                            timestamp = it.timestamp.formatDateTime(),
                            mediaType = it.mediaType,
                            trackStatus = it.status!!,
                            rating = it.rating,
                            reviewTitle = it.reviewTitle,
                            reviewDescription = it.reviewDescription,
                            onUserClick = { onUserClick(it.userId) },
                        )
                    }
                }
            }*/

            if (friendTrackings.isNotEmpty() || receivedRecommendations.isNotEmpty()) {
                item {
                    FriendActivityRow(
                        friendTrackings = friendTrackings,
                        friendRecommendations = receivedRecommendations,
                        onUserClick = onUserClick,
                        onMoreClick = onShowFriendActivity
                    )
                }
            }

            item {
                RatingCardRow(
                    apiType = media.mediaType.getRatingType(),
                    rating = media.apiRating,
                    ratingCount = media.apiRatingCount,
                    appRating = ratingStats.averageRating,
                    appRatingCount = ratingStats.ratingCount
                )
            }

            when (media.mediaType) {
                MediaType.MOVIE -> MovieDetailContent(movie = media as Movie, onClickItem = onClickItem)
                MediaType.SHOW -> ShowDetailContent(show = media as Show, onClickItem = onClickItem)
                MediaType.BOOK -> BookDetailContent(book = media as Book, onClickItem = onClickItem)
                MediaType.VIDEOGAME -> VideogameDetailContent(videogame = media as Videogame, onClickItem = onClickItem)
                MediaType.BOARDGAME -> BoardgameDetailContent(boardgame = media as Boardgame, onClickItem = onClickItem)
                MediaType.ALBUM -> AlbumDetailContent(album = media as Album, onClickItem = onClickItem)
            }
        }
    }
}
