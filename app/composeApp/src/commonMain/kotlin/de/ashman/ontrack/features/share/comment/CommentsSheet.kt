package de.ashman.ontrack.features.share.comment

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
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
import de.ashman.ontrack.features.common.CommentTextField
import de.ashman.ontrack.features.common.OnTrackIconButton
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.formatDateTime
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_comments
import ontrack.composeapp.generated.resources.share_comments_empty
import ontrack.composeapp.generated.resources.share_comments_placeholder
import org.jetbrains.compose.resources.stringResource

// TODO maybe remove this sheet altogether
@Composable
fun CommentsSheet(
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onRemoveComment: (Comment) -> Unit,
    onClickUser: (String) -> Unit,
) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var replyingTo by remember { mutableStateOf<String?>(null) }

    var showCommentRemoveConfirmDialog by remember { mutableStateOf(false) }

    val localFocusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { localFocusManager.clearFocus() })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.share_comments, comments.size, comments.size),
            style = MaterialTheme.typography.titleMedium,
        )

        AnimatedContent(
            modifier = Modifier
                .weight(1f, false)
                .heightIn(max = 300.dp),
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
                            userImageUrl = it.userImageUrl,
                            name = it.name,
                            timestamp = it.timestamp.formatDateTime(),
                            comment = it.comment,
                            onShowRemoveCommentConfirmDialog = {
                                showCommentRemoveConfirmDialog = true
                            },
                            onReply = {
                                val newText = "@${it.username} "
                                commentText = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(newText.length)
                                )
                                focusRequester.requestFocus()
                            },
                            onClickUser = { onClickUser(it.userId) },
                            isOwnComment = it.userId == Firebase.auth.currentUser?.uid,
                        )

                        if (showCommentRemoveConfirmDialog) {
                            RemoveCommentConfirmDialog(
                                onConfirm = {
                                    onRemoveComment(it)
                                    showCommentRemoveConfirmDialog = false
                                },
                                onDismiss = { showCommentRemoveConfirmDialog = false },
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CommentTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .weight(1f),
                placeholder = stringResource(Res.string.share_comments_placeholder),
                value = commentText,
                onValueChange = { commentText = it },
                onPostComment = {},
            )

            OnTrackIconButton(
                modifier = Modifier.size(56.dp),
                icon = Icons.AutoMirrored.Default.Send,
                enabled = commentText.text.isNotBlank(),
                onClick = {
                    onAddComment(commentText.text)
                    replyingTo = null
                    commentText = TextFieldValue("")
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentCard(
    modifier: Modifier = Modifier,
    userImageUrl: String,
    name: String,
    timestamp: String,
    comment: String,
    onShowRemoveCommentConfirmDialog: () -> Unit,
    onReply: () -> Unit,
    onClickUser: () -> Unit,
    isOwnComment: Boolean = false,
) {
    val annotatedString = buildAnnotatedString {
        append(comment)
        val regex = "@\\S+".toRegex()
        regex.findAll(comment).forEach { matchResult ->
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
                annotation = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                modifier.combinedClickable(
                    onClick = {},
                    onLongClick = { if (isOwnComment) onShowRemoveCommentConfirmDialog() },
                ),
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PersonImage(
                    userImageUrl = userImageUrl,
                    onClick = onClickUser
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!isOwnComment) {
                    IconButton(onClick = onReply) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Reply,
                            contentDescription = "Reply Icon",
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.padding(start = 56.dp),
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}