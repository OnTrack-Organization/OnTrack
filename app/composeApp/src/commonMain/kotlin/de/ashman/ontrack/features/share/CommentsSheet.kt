package de.ashman.ontrack.features.share

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SendMessageTextField
import de.ashman.ontrack.features.notification.formatTimeAgoString
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_comments
import ontrack.composeapp.generated.resources.share_comments_count
import ontrack.composeapp.generated.resources.share_comments_empty
import ontrack.composeapp.generated.resources.share_comments_placeholder
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentsSheet(
    comments: List<Comment>,
    commentCount: Int,
    sendingComment: Boolean,
    onPostComment: (String) -> Unit,
    onRemoveComment: (String) -> Unit,
    onClickUser: (String) -> Unit,
) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var commentIdToRemove by remember { mutableStateOf<String?>(null) }
    var shouldScrollAfterComment by remember { mutableStateOf(false) }

    val localFocusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    LaunchedEffect(comments.size) {
        if (shouldScrollAfterComment) {
            listState.animateScrollToItem(comments.lastIndex)
            shouldScrollAfterComment = false
        }
    }

    /*LaunchedEffect(listState, comments.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            val lastVisibleItem = visibleItems.lastOrNull()
            val totalItemsCount = layoutInfo.totalItemsCount
            lastVisibleItem?.index == totalItemsCount - 1
        }
            .distinctUntilChanged()
            .collect { isAtEnd ->
                if (isAtEnd) {
                    onFetchNextPage()
                }
            }
    }*/

    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { localFocusManager.clearFocus() })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = if (commentCount == 0) stringResource(Res.string.share_comments) else pluralStringResource(Res.plurals.share_comments_count, commentCount, commentCount),
            style = MaterialTheme.typography.titleMedium,
        )

        AnimatedContent(
            modifier = Modifier
                .weight(1f, false)
                .heightIn(max = 500.dp),
            targetState = comments.isNotEmpty(),
            label = "Comment List Animation"
        ) { hasComments ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState,
            ) {
                if (!hasComments) {
                    item {
                        Text(
                            text = stringResource(Res.string.share_comments_empty),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(items = comments, key = { it.id }) {
                        CommentCard(
                            modifier = Modifier.animateItem(),
                            comment = it,
                            onShowRemoveCommentConfirmDialog = { commentIdToRemove = it.id },
                            onReply = {
                                val newText = "@${it.user.username} "
                                commentText = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(newText.length)
                                )
                                focusRequester.requestFocus()
                            },
                            onClickUser = { onClickUser(it.user.id) },
                            byCurrentUser = it.postedByCurrentUser,
                            isDeletable = it.deletable,
                            onClickMentionedUser = { userId -> onClickUser(userId) },
                        )
                    }

                    /*if (postResultState == PostResultState.LoadingMore) {
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
                    }*/
                }
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

        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SendMessageTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .weight(1f),
                placeholder = stringResource(Res.string.share_comments_placeholder),
                value = commentText,
                onValueChange = {
                    commentText = it
                },
                isSendVisible = commentText.text.isNotBlank(),
                isSending = sendingComment,
                onSend = {
                    onPostComment(commentText.text)
                    commentText = TextFieldValue("")
                    shouldScrollAfterComment = true
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentCard(
    modifier: Modifier = Modifier,
    comment: Comment,
    byCurrentUser: Boolean,
    isDeletable: Boolean,
    onReply: () -> Unit,
    onClickUser: () -> Unit,
    onClickMentionedUser: (String) -> Unit,
    onShowRemoveCommentConfirmDialog: () -> Unit,
) {
    // Map "@username" to userId
    val mentionMap = comment.mentionedUsers.associateBy { "@${it.username}" }

    // Remove trailing spaces from the message
    val trimmedMessage = comment.message.trimEnd()

    val annotatedString = buildAnnotatedString {
        append(trimmedMessage)
        val regex = "@\\S+".toRegex()
        regex.findAll(trimmedMessage).forEach { matchResult ->
            val usernameTag = matchResult.value
            mentionMap[usernameTag]?.let { user ->
                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    start = matchResult.range.first,
                    end = matchResult.range.last + 1
                )
                addStringAnnotation(
                    tag = "USERNAME",
                    annotation = user.id,
                    start = matchResult.range.first,
                    end = matchResult.range.last + 1
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = { if (isDeletable) onShowRemoveCommentConfirmDialog() },
                )
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PersonImage(
                    profilePictureUrl = comment.user.profilePictureUrl,
                    onClick = onClickUser
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = comment.user.username,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = comment.timestamp.formatTimeAgoString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!byCurrentUser) {
                    IconButton(onClick = onReply) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Reply,
                            contentDescription = "Reply Icon",
                        )
                    }
                }
            }

            ClickableText(
                modifier = Modifier.padding(start = 56.dp),
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    annotatedString
                        .getStringAnnotations("USERNAME", offset, offset)
                        .firstOrNull()
                        ?.let { annotation ->
                            onClickMentionedUser(annotation.item)
                        }
                }
            )
        }
    }
}
