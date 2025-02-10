package de.ashman.ontrack.features.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.TrackingComment
import de.ashman.ontrack.features.common.OnTrackIconButton
import de.ashman.ontrack.features.common.OnTrackTextField
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_comments
import ontrack.composeapp.generated.resources.feed_comments_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentsContent(
    comments: List<TrackingComment>,
    onAddComment: (String) -> Unit,
    onDeleteComment: (String) -> Unit,
) {
    var commentString by remember { mutableStateOf("") }
    var showDeleteCommentDialog by remember { mutableStateOf(false) }

    val localFocusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.feed_comments),
            style = MaterialTheme.typography.titleMedium,
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.5f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState,
            contentPadding = PaddingValues(16.dp),
        ) {
            items(items = comments, key = { it.id }) {
                FeedCardComment(
                    userImageUrl = it.userImageUrl,
                    username = it.username,
                    comment = it.comment,
                    showDeleteCommentDialog = { showDeleteCommentDialog  = true},
                )

                if (showDeleteCommentDialog) {
                    DeleteCommentDialog(
                        onConfirmDelete = {
                            onDeleteComment(it.id)
                            showDeleteCommentDialog = false
                        },
                        onDismiss = { showDeleteCommentDialog = false },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OnTrackTextField(
                modifier = Modifier.weight(1f),
                placeholder = stringResource(Res.string.feed_comments_placeholder),
                value = commentString,
                onValueChange = { commentString = it },
            )
            OnTrackIconButton(
                icon = Icons.AutoMirrored.Default.Send,
                enabled = commentString.isNotBlank(),
                onClick = {
                    if (commentString.isNotBlank()) {
                        onAddComment(commentString)
                        commentString = ""
                        localFocusManager.clearFocus()

                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                index = comments.size - 1,
                            )
                        }
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedCardComment(
    userImageUrl: String,
    username: String,
    comment: String,
    showDeleteCommentDialog: () -> Unit,
) {
    val painter = rememberAsyncImagePainter(userImageUrl)

    Column(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = showDeleteCommentDialog,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                val state = painter.state.collectAsState().value

                when (state) {
                    is AsyncImagePainter.State.Empty -> {}
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = painter,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Account Image",
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            imageVector = Icons.Default.Person,
                            contentDescription = "No Image",
                        )
                    }
                }
            }
            Text(
                text = username,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = comment,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}