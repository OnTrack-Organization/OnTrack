package de.ashman.ontrack.features.detail

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import de.ashman.ontrack.api.getApiType
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.media.Album
import de.ashman.ontrack.domain.media.Boardgame
import de.ashman.ontrack.domain.media.Book
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.media.Movie
import de.ashman.ontrack.domain.media.Show
import de.ashman.ontrack.domain.media.Videogame
import de.ashman.ontrack.domain.recommendation.FriendsActivity
import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.ErrorContent
import de.ashman.ontrack.features.common.LargerImageDialog
import de.ashman.ontrack.features.common.LoadingContent
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.RemoveSheet
import de.ashman.ontrack.features.detail.components.DetailDropDown
import de.ashman.ontrack.features.detail.components.RatingCardRow
import de.ashman.ontrack.features.detail.components.StickyHeader
import de.ashman.ontrack.features.detail.media.AlbumDetailContent
import de.ashman.ontrack.features.detail.media.BoardgameDetailContent
import de.ashman.ontrack.features.detail.media.BookDetailContent
import de.ashman.ontrack.features.detail.media.MovieDetailContent
import de.ashman.ontrack.features.detail.media.ShowDetailContent
import de.ashman.ontrack.features.detail.media.VideogameDetailContent
import de.ashman.ontrack.features.detail.recommendation.FriendsActivityRow
import de.ashman.ontrack.features.detail.recommendation.FriendsActivitySheet
import de.ashman.ontrack.features.detail.recommendation.RecommendSheet
import de.ashman.ontrack.features.detail.tracking.TrackSheet
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_remove_confirm_text
import ontrack.composeapp.generated.resources.detail_remove_confirm_title
import org.jetbrains.compose.resources.pluralStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mediaNav: MediaNavigationParam,
    commonUiManager: CommonUiManager,
    detailViewModel: DetailViewModel,
    onClickItem: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onBack: () -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsStateWithLifecycle()
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    val localFocusManager = LocalFocusManager.current

    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(mediaNav.id) {
        detailViewModel.fetchDetails(mediaNav)
        detailViewModel.observeTracking(mediaNav.id, mediaNav.type)
        detailViewModel.fetchFriends()
        detailViewModel.fetchFriendsActivity(mediaNav.type, mediaNav.id)
        //detailViewModel.observeRatingStats(mediaNav.id, mediaNav.mediaType)
    }

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            OnTrackTopBar(
                title = pluralStringResource(mediaNav.type.getMediaTypeUi().title, 1),
                titleIcon = mediaNav.type.getMediaTypeUi().outlinedIcon,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                onClickNavigation = onBack,
                dropdownMenu = {
                    DetailDropDown(
                        isRemoveEnabled = detailUiState.currentTracking != null,
                        mediaApiUrl = detailUiState.currentMedia?.detailUrl,
                        apiTitle = mediaNav.type.getApiType().title,
                        apiIcon = mediaNav.type.getApiType().icon,
                        onClickRemove = { commonUiManager.showSheet(CurrentSheet.REMOVE) }
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
                media = detailUiState.currentMedia,
                mediaType = mediaNav.type,
                mediaTitle = mediaNav.title,
                mediaCoverUrl = mediaNav.coverUrl,
                status = detailUiState.currentTracking?.status,
                scrollBehavior = scrollBehavior,
                onClickAddTracking = { commonUiManager.showSheet(CurrentSheet.TRACK) },
                onClickRecommend = { commonUiManager.showSheet(CurrentSheet.RECOMMEND) },
                onClickImage = { imageUrl ->
                    selectedImageUrl = imageUrl
                    showImageDialog = true
                }
            )

            when (detailUiState.apiResultState) {
                ApiResultState.ApiLoading -> LoadingContent()

                ApiResultState.ApiError -> ErrorContent(text = mediaNav.type.getMediaTypeUi().error)

                ApiResultState.ApiSuccess -> {
                    DetailContent(
                        media = detailUiState.currentMedia,
                        currentTracking = detailUiState.currentTracking,
                        ratingStats = detailUiState.ratingStats,
                        friendsActivity = detailUiState.friendsActivity,
                        columnListState = listState,
                        onClickMedia = onClickItem,
                        onClickUser = onClickUser,
                        onClickMoreFriendsActivity = { commonUiManager.showSheet(CurrentSheet.FRIEND_ACTIVITY) },
                        onClickImage = { imageUrl ->
                            selectedImageUrl = imageUrl
                            showImageDialog = true
                        },
                        onClickReview = { commonUiManager.showSheet(CurrentSheet.REVIEW) },
                        onClickTimeline = { commonUiManager.showSheet(CurrentSheet.TIMELINE) },
                    )
                }
            }
        }

        if (showImageDialog) {
            LargerImageDialog(
                showDialog = showImageDialog,
                imageUrl = selectedImageUrl,
                onDismiss = { showImageDialog = false },
            )
        }

        if (commonUiState.showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    commonUiManager.hideSheet()
                    detailViewModel.selectStatus(detailUiState.currentTracking?.status)
                },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (commonUiState.currentSheet == CurrentSheet.RECOMMEND) 0.dp else 16.dp)
                        .padding(bottom = 16.dp)
                        .imePadding()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                localFocusManager.clearFocus()
                            })
                        },
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (commonUiState.currentSheet) {
                        CurrentSheet.TRACK -> TrackSheet(
                            mediaType = mediaNav.type,
                            selectedStatus = detailUiState.currentStatus,
                            isSaving = detailUiState.resultState == DetailResultState.Loading,
                            onSelectStatus = detailViewModel::selectStatus,
                            onSave = detailViewModel::saveTracking,
                            onToReview = { commonUiManager.showSheet(CurrentSheet.REVIEW) },
                        )

                        // TODO add in the next step when adding review back in again
                        /*CurrentSheet.REVIEW -> ReviewSheet(
                            reviewTitle = tracking.reviewTitle,
                            reviewDescription = tracking.reviewDescription,
                            rating = tracking.rating,
                            trackStatus = tracking.status,
                            onReviewTitleChange = { title ->
                                tracking = tracking.copy(reviewTitle = title.takeIf { it.isNotBlank() })
                            },
                            onReviewDescriptionChange = { description ->
                                tracking = tracking.copy(reviewDescription = description.takeIf { it.isNotBlank() })
                            },
                            onRatingChange = { tracking = tracking.copy(rating = it) },
                            onSave = { detailViewModel.createTracking(tracking) },
                        )*/

                        CurrentSheet.REMOVE -> RemoveSheet(
                            title = Res.string.detail_remove_confirm_title,
                            text = Res.string.detail_remove_confirm_text,
                            isDeleting = detailUiState.resultState == DetailResultState.Loading,
                            onConfirm = detailViewModel::removeTracking,
                            onCancel = { commonUiManager.hideSheet() },
                        )

                        CurrentSheet.FRIEND_ACTIVITY -> FriendsActivitySheet(
                            friendsActivity = detailUiState.friendsActivity,
                            isMediaUntracked = detailUiState.currentTracking == null,
                            mediaType = mediaNav.type,
                            isAddingToCatalog = detailUiState.resultState == DetailResultState.Loading,
                            onUserClick = onClickUser,
                            onCatalogRecommendation = detailViewModel::catalogRecommendation,
                        )

                        CurrentSheet.RECOMMEND -> RecommendSheet(
                            resultState = detailUiState.resultState,
                            friends = detailUiState.friends,
                            sentRecommendations = detailUiState.sentRecommendations,
                            fetchSentRecommendations = { userId ->
                                detailViewModel.fetchSentRecommendations(mediaNav.type, mediaNav.id, userId)
                            },
                            onSendRecommendation = detailViewModel::sendRecommendation,
                            onClickUser = onClickUser,
                        )

                        // TODO add in later again
                        /*CurrentSheet.TIMELINE -> TimelineSheet(
                            mediaType = mediaNav.mediaType,
                            entries = tracking.history,
                        )*/

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
    currentTracking: NewTracking?,
    ratingStats: RatingStats,
    friendsActivity: FriendsActivity?,
    columnListState: LazyListState,
    onClickMedia: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onClickMoreFriendsActivity: () -> Unit,
    onClickImage: (String) -> Unit,
    onClickReview: () -> Unit,
    onClickTimeline: () -> Unit,
) {
    media?.let {
        LazyColumn(
            state = columnListState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // TODO add in back again later
            /*currentTracking?.let { tracking ->
                tracking.rating?.let {
                    item {
                        OwnTrackingCard(
                            trackStatus = tracking.status!!,
                            rating = tracking.rating,
                            reviewTitle = tracking.reviewTitle,
                            reviewDescription = tracking.reviewDescription,
                            onClickTimeline = onClickTimeline,
                            onClickCard = onClickReview
                        )
                    }
                }
            }*/

            friendsActivity?.let {
                item {
                    FriendsActivityRow(
                        friendsActivity = friendsActivity,
                        onClickUser = onClickUser,
                        onClickMore = onClickMoreFriendsActivity
                    )
                }
            }

            item {
                RatingCardRow(
                    apiType = media.mediaType.getApiType(),
                    rating = media.apiRating,
                    ratingCount = media.apiRatingCount,
                    appRating = ratingStats.averageRating,
                    appRatingCount = ratingStats.ratingCount
                )
            }

            when (media.mediaType) {
                MediaType.MOVIE -> MovieDetailContent(movie = media as Movie, onClickItem = onClickMedia, onClickImage = onClickImage)
                MediaType.SHOW -> ShowDetailContent(show = media as Show, onClickItem = onClickMedia, onClickImage = onClickImage)
                MediaType.BOOK -> BookDetailContent(book = media as Book, onClickItem = onClickMedia)
                MediaType.VIDEOGAME -> VideogameDetailContent(videogame = media as Videogame, onClickItem = onClickMedia, onClickImage = onClickImage)
                MediaType.BOARDGAME -> BoardgameDetailContent(boardgame = media as Boardgame, onClickItem = onClickMedia, onClickImage = onClickImage)
                MediaType.ALBUM -> AlbumDetailContent(album = media as Album, onClickItem = onClickMedia)
            }
        }
    }
}
