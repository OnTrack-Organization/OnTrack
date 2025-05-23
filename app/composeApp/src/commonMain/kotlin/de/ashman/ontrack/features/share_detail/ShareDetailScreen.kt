package de.ashman.ontrack.features.share_detail

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.common.SendMessageTextField
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.notifications.formatTimeAgoString
import de.ashman.ontrack.features.share.ShareCardHeader
import de.ashman.ontrack.features.share.ShareCardMediaTitle
import de.ashman.ontrack.features.share.ShareCardReview
import de.ashman.ontrack.features.share.comment.CommentCard
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_comments
import ontrack.composeapp.generated.resources.share_comments_empty
import ontrack.composeapp.generated.resources.share_comments_placeholder
import ontrack.composeapp.generated.resources.share_detail_title
import ontrack.composeapp.generated.resources.share_likes
import ontrack.composeapp.generated.resources.share_likes_empty
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDetailScreen(
    viewModel: ShareDetailViewModel,
    // Todo maybe pass tracking instead
    trackingId: String,
    onClickMedia: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(trackingId) {
        viewModel.fetchTracking(trackingId)
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.share_detail_title),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                actionIcon = Icons.Default.MoreVert,
                onClickNavigation = onBack,
                onClickAction = { /*TODO*/ }
            )
        },
    ) { contentPadding ->
        uiState.tracking?.let {
            ShareDetailContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .imePadding()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            localFocusManager.clearFocus()
                        })
                    },
                tracking = it,
                isSending = uiState.resultState == ShareDetailResultState.Loading,
                onClickCover = onClickMedia,
                onClickUser = onClickUser,
                // TODO
                onClickLike = {},
                onPostComment = {},
            )
        }
    }
}

@Composable
fun ShareDetailContent(
    modifier: Modifier = Modifier,
    tracking: Tracking,
    isSending: Boolean = false,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onClickLike: () -> Unit,
    onPostComment: (String) -> Unit,
) {
    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ShareDetailMainContent(
                    tracking = tracking,
                    onClickCover = onClickCover,
                    onClickUser = onClickUser,
                )
            }

            item { HorizontalDivider() }

            item {
                ShareDetailLikeContent(
                    isLiked = false,
                    likes = tracking.likes,
                    onClickUser = onClickUser,
                    onClickLike = onClickLike,
                )
            }

            item { HorizontalDivider() }

            item {
                ShareDetailCommentContent(
                    modifier = Modifier.weight(1f),
                    comments = tracking.comments,
                    onClickUser = onClickUser,
                )
            }
        }

        // TODO move into vm
        var commentText by remember { mutableStateOf(TextFieldValue("test")) }
        var replyingTo by remember { mutableStateOf<String?>(null) }
        val focusRequester = remember { FocusRequester() }

        SendMessageTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            placeholder = stringResource(Res.string.share_comments_placeholder),
            value = commentText,
            onValueChange = { commentText = it },
            isSendVisible = commentText.text.isNotBlank(),
            isSending = isSending,
            onSend = {
                onPostComment(commentText.text)
                replyingTo = null
                commentText = TextFieldValue("")
            },
        )
    }
}

@Composable
fun ShareDetailMainContent(
    tracking: Tracking,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                ShareCardHeader(
                    profilePictureUrl = tracking.userImageUrl,
                    username = tracking.username,
                    timestamp = tracking.timestamp,
                    mediaType = tracking.mediaType,
                    trackStatus = tracking.status!!,
                    onClickUser = { onClickUser(tracking.userId) },
                )

                ShareCardMediaTitle(
                    mediaType = tracking.mediaType,
                    mediaTitle = tracking.mediaTitle,
                )
            }

            MediaPoster(
                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                coverUrl = tracking.mediaCoverUrl,
                onClick = {
                    onClickCover(
                        MediaNavigationParam(
                            id = tracking.mediaId,
                            type = tracking.mediaType,
                            title = tracking.mediaTitle,
                            coverUrl = tracking.mediaCoverUrl,
                        )
                    )
                },
            )
        }

        ShareCardReview(
            reviewTitle = tracking.reviewTitle,
            reviewDescription = tracking.reviewDescription,
            reviewRating = tracking.rating,
            starColor = contentColorFor(tracking.status.getColor()),
        )
    }
}

@Composable
fun ShareDetailLikeContent(
    isLiked: Boolean,
    likes: List<Like>,
    onClickUser: (String) -> Unit,
    onClickLike: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.share_likes, likes.size, likes.size),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (likes.isEmpty()) {
                Spacer(modifier = Modifier.width(48.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.share_likes_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyRow(
                    modifier = Modifier.weight(1f),
                ) {
                    items(likes) {
                        UserLikeComponent(
                            imageUrl = it.userImageUrl,
                            name = it.name,
                            onClickUser = { onClickUser(it.userId) },
                        )
                    }
                }
            }

            IconButton(
                onClick = onClickLike,
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun UserLikeComponent(
    imageUrl: String?,
    name: String,
    onClickUser: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PersonImage(
            profilePictureUrl = imageUrl,
            onClick = onClickUser,
        )

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(64.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ShareDetailCommentContent(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
    onClickUser: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.share_comments, comments.size, comments.size),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (comments.isEmpty()) {
            // TODO make this centered vertically
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.share_comments_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                comments.forEach {
                    CommentCard(
                        userImageUrl = it.userImageUrl,
                        name = it.name,
                        timestamp = it.timestamp.formatTimeAgoString(),
                        comment = it.comment,
                        onShowRemoveCommentConfirmDialog = {},
                        onReply = {},
                        onClickUser = { onClickUser(it.userId) },
                    )
                }
            }
        }
    }
}
