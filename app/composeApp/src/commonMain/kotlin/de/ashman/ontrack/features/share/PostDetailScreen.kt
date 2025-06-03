package de.ashman.ontrack.features.share

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RateReview
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.common.SendMessageTextField
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_comments
import ontrack.composeapp.generated.resources.share_comments_count
import ontrack.composeapp.generated.resources.share_comments_empty
import ontrack.composeapp.generated.resources.share_comments_placeholder
import ontrack.composeapp.generated.resources.share_detail_title
import ontrack.composeapp.generated.resources.share_likes
import ontrack.composeapp.generated.resources.share_likes_count
import ontrack.composeapp.generated.resources.share_likes_empty
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: PostViewModel,
    postId: String,
    onClickMedia: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(postId) {
        viewModel.fetchPost(postId)
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.share_detail_title),
                titleIcon = Icons.Default.RateReview,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                actionIcon = Icons.Default.MoreVert,
                onClickNavigation = onBack,
                onClickAction = { /*TODO*/ }
            )
        },
    ) { contentPadding ->
        uiState.selectedPost?.let {
            PostDetailContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .imePadding()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            localFocusManager.clearFocus()
                        })
                    },
                post = it,
                // TODO
                isSending = false,
                onClickCover = onClickMedia,
                onClickUser = onClickUser,
                onClickLike = { viewModel.toggleLike(postId) },
                onPostComment = viewModel::addComment,
                onRemoveComment = viewModel::removeComment,
            )
        }
    }
}

@Composable
fun PostDetailContent(
    modifier: Modifier = Modifier,
    post: Post,
    isSending: Boolean = false,
    onClickCover: (MediaNavigationParam) -> Unit = {},
    onClickUser: (String) -> Unit = {},
    onClickLike: () -> Unit = {},
    onPostComment: (String) -> Unit = {},
    onRemoveComment: (String) -> Unit = {},
) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var shouldScrollAfterComment by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    LaunchedEffect(post.comments.size) {
        if (shouldScrollAfterComment) {
            listState.animateScrollToItem(post.comments.lastIndex)
            shouldScrollAfterComment = false
        }
    }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PostDetailMainContent(
                    post = post,
                    onClickCover = onClickCover,
                    onClickUser = onClickUser,
                )
            }

            item { HorizontalDivider() }

            item {
                PostDetailLikeContent(
                    isLiked = post.likedByCurrentUser,
                    likes = post.likes,
                    likeCount = post.likeCount,
                    onClickUser = onClickUser,
                    onClickLike = onClickLike,
                )
            }

            item { HorizontalDivider() }

            item {
                PostDetailCommentContent(
                    modifier = Modifier.weight(1f),
                    comments = post.comments,
                    commentCount = post.commentCount,
                    onClickUser = onClickUser,
                    onReply = {
                        val newText = "@${it} "
                        commentText = TextFieldValue(
                            text = newText,
                            selection = TextRange(newText.length)
                        )
                        focusRequester.requestFocus()
                    },
                    onRemoveComment = onRemoveComment,
                )
            }
        }

        SendMessageTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .focusRequester(focusRequester),
            placeholder = stringResource(Res.string.share_comments_placeholder),
            value = commentText,
            onValueChange = { commentText = it },
            isSendVisible = commentText.text.isNotBlank(),
            isSending = isSending,
            onSend = {
                onPostComment(commentText.text)
                commentText = TextFieldValue("")

                shouldScrollAfterComment = true
            },
        )
    }
}

@Composable
fun PostDetailMainContent(
    post: Post,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
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
                    profilePictureUrl = post.user.profilePictureUrl,
                    username = post.user.username,
                    timestamp = post.tracking.timestamp,
                    mediaType = post.tracking.media.type,
                    trackStatus = post.tracking.status,
                    onClickUser = { onClickUser(post.user.id) },
                )

                ShareCardMediaTitle(
                    mediaType = post.tracking.media.type,
                    mediaTitle = post.tracking.media.title,
                )
            }

            MediaPoster(
                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                coverUrl = post.tracking.media.coverUrl,
                onClick = {
                    onClickCover(
                        MediaNavigationParam(
                            id = post.tracking.media.id,
                            type = post.tracking.media.type,
                            title = post.tracking.media.title,
                            coverUrl = post.tracking.media.coverUrl,
                        )
                    )
                },
            )
        }

        ShareCardReview(
            review = post.review,
            starColor = contentColorFor(post.tracking.status.getColor()),
        )
    }
}

@Composable
fun PostDetailLikeContent(
    isLiked: Boolean,
    likes: List<Like>,
    likeCount: Int,
    onClickUser: (String) -> Unit,
    onClickLike: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (likeCount == 0) stringResource(Res.string.share_likes) else pluralStringResource(Res.plurals.share_likes_count, likeCount, likeCount),
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
                            profilePictureUrl = it.user.profilePictureUrl,
                            username = it.user.username,
                            onClickUser = { onClickUser(it.user.id) },
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
fun PostDetailCommentContent(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
    commentCount: Int,
    onClickUser: (String) -> Unit,
    onReply: (String) -> Unit,
    onRemoveComment: (String) -> Unit,
) {
    var commentIdToRemove by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (commentCount == 0) stringResource(Res.string.share_comments) else pluralStringResource(Res.plurals.share_comments_count, commentCount, commentCount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        if (comments.isEmpty()) {
            Text(
                text = stringResource(Res.string.share_comments_empty),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        } else {
            comments.forEach { comment ->
                CommentCard(
                    comment = comment,
                    onClickUser = { onClickUser(comment.user.id) },
                    onReply = { onReply(comment.user.username) },
                    onShowRemoveCommentConfirmDialog = { commentIdToRemove = comment.id },
                    byCurrentUser = comment.postedByCurrentUser,
                    isDeletable = comment.deletable,
                )
            }
        }

        if (commentIdToRemove != null) {
            RemoveCommentConfirmDialog(
                onConfirm = {
                    commentIdToRemove?.let { onRemoveComment(it) }
                    commentIdToRemove = null
                },
                onDismiss = {
                    commentIdToRemove = null
                },
            )
        }
    }
}

@Composable
fun UserLikeComponent(
    profilePictureUrl: String?,
    username: String,
    onClickUser: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PersonImage(
            profilePictureUrl = profilePictureUrl,
            onClick = onClickUser,
        )

        Text(
            text = username,
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
