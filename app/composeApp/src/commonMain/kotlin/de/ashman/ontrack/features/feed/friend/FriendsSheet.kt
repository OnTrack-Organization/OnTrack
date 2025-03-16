package de.ashman.ontrack.features.feed.friend

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.toRequest
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.SearchBar
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.feed_friends
import ontrack.app.composeapp.generated.resources.feed_no_friends_and_potential
import ontrack.app.composeapp.generated.resources.feed_no_potential_friends
import ontrack.app.composeapp.generated.resources.feed_potential_friends
import ontrack.app.composeapp.generated.resources.feed_received_requests
import ontrack.app.composeapp.generated.resources.feed_sent_requests
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsSheet(
    uiState: FriendsUiState,
    isRequestSent: (String) -> Boolean,
    onRemoveFriend: () -> Unit,
    onSelectFriend: (Friend) -> Unit,
    onAcceptRequest: (FriendRequest) -> Unit,
    onDeclineRequest: (FriendRequest) -> Unit,
    onCancelRequest: (FriendRequest) -> Unit,
    onSendRequest: (FriendRequest) -> Unit,
    onClickUser: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
) {
    var showFriendRemoveConfirmDialog by remember { mutableStateOf(false) }

    val localFocusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SearchBar(
            query = uiState.query,
            onQueryChanged = onQueryChanged,
            placeholder = stringResource(Res.string.feed_friends).lowercase(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            closeKeyboard = { localFocusManager.clearFocus() },
        )

        AnimatedContent(uiState.resultState) { state ->
            when (state) {
                FriendsResultState.Friends -> {
                    FriendsAndRequests(
                        receivedRequests = uiState.receivedRequests,
                        sentRequests = uiState.sentRequests,
                        friends = uiState.friends,
                        onAcceptRequest = onAcceptRequest,
                        onDenyRequest = onDeclineRequest,
                        onCancelRequest = onCancelRequest,
                        onRemoveFriend = {
                            onSelectFriend(it)
                            showFriendRemoveConfirmDialog = true
                        },
                        onClickUser = onClickUser,
                    )
                }

                FriendsResultState.FriendsEmpty -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(Res.string.feed_no_friends_and_potential),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FriendsResultState.Potential -> {
                    PotentialFriends(
                        potentialFriends = uiState.potentialFriends,
                        isRequestSent = isRequestSent,
                        onCancelRequest = onCancelRequest,
                        onSendRequest = onSendRequest,
                        onClickUser = onClickUser,
                    )
                }

                FriendsResultState.PotentialEmpty -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(Res.string.feed_no_potential_friends),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (showFriendRemoveConfirmDialog) {
            RemoveFriendConfirmDialog(
                onConfirm = {
                    onRemoveFriend()
                    showFriendRemoveConfirmDialog = false
                },
                onDismiss = { showFriendRemoveConfirmDialog = false },
            )
        }
    }
}

@Composable
fun PotentialFriends(
    potentialFriends: List<Friend>,
    isRequestSent: (String) -> Boolean,
    onSendRequest: (FriendRequest) -> Unit,
    onCancelRequest: (FriendRequest) -> Unit,
    onClickUser: (String) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(Res.string.feed_potential_friends),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        items(potentialFriends.size) { index ->
            val friend = potentialFriends[index]

            PotentialFriendCard(
                imageUrl = friend.imageUrl,
                username = friend.username,
                name = friend.name,
                onClickUser = { onClickUser(friend.id) },
                onSendRequest = { onSendRequest(friend.toRequest()) },
                onCancelRequest = { onCancelRequest(friend.toRequest()) },
                isRequestSent = isRequestSent(friend.id),
            )
        }
    }
}

@Composable
fun FriendsAndRequests(
    receivedRequests: List<FriendRequest>,
    sentRequests: List<FriendRequest>,
    friends: List<Friend>,
    onAcceptRequest: (FriendRequest) -> Unit,
    onDenyRequest: (FriendRequest) -> Unit,
    onCancelRequest: (FriendRequest) -> Unit,
    onRemoveFriend: (Friend) -> Unit,
    onClickUser: (String) -> Unit,
) {
    LazyColumn {
        if (receivedRequests.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.feed_received_requests),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            items(receivedRequests.size) { index ->
                val request = receivedRequests[index]
                ReceivedFriendRequestCard(
                    imageUrl = request.imageUrl,
                    username = request.username,
                    name = request.name,
                    onClickUser = { onClickUser(request.userId) },
                    onAcceptRequest = { onAcceptRequest(request) },
                    onDenyRequest = { onDenyRequest(request) },
                )
            }
        }

        if (friends.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.feed_friends),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            items(friends.size) { index ->
                val friend = friends[index]
                CurrentFriendCard(
                    imageUrl = friend.imageUrl,
                    username = friend.username,
                    name = friend.name,
                    onClickUser = { onClickUser(friend.id) },
                    onRemoveFriend = { onRemoveFriend(friend) },
                )
            }
        }

        if (sentRequests.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.feed_sent_requests),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            items(sentRequests.size) { index ->
                val request = sentRequests[index]
                SentFriendRequestCard(
                    imageUrl = request.imageUrl,
                    username = request.username,
                    name = request.name,
                    onClickUser = { onClickUser(request.userId) },
                    onCancelRequest = { onCancelRequest(request) },
                )
            }
        }

        if (friends.isEmpty() && receivedRequests.isEmpty() && sentRequests.isEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(Res.string.feed_no_friends_and_potential),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

