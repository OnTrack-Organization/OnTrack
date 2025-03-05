package de.ashman.ontrack.features.feed.friend

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_friends
import ontrack.composeapp.generated.resources.feed_no_friends_and_potential
import ontrack.composeapp.generated.resources.feed_no_potential_friends
import ontrack.composeapp.generated.resources.feed_potential_friends
import ontrack.composeapp.generated.resources.feed_received_requests
import ontrack.composeapp.generated.resources.feed_sent_requests
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsSheet(
    uiState: FriendsUiState,
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
    val listState = rememberLazyListState()

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
        SearchBar(
            query = uiState.query,
            onQueryChanged = onQueryChanged,
            placeholder = stringResource(Res.string.feed_friends).lowercase(),
            closeKeyboard = { localFocusManager.clearFocus() },
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            item {
                when (uiState.resultState) {
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
                            sentRequests = uiState.sentRequests,
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
    sentRequests: List<FriendRequest>,
    onSendRequest: (FriendRequest) -> Unit,
    onCancelRequest: (FriendRequest) -> Unit,
    onClickUser: (String) -> Unit,
) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(Res.string.feed_potential_friends),
        style = MaterialTheme.typography.titleMedium,
    )

    potentialFriends.forEach { friend ->
        val isRequestSent = sentRequests.any { it.userId == friend.id }

        PotentialFriendCard(
            imageUrl = friend.imageUrl,
            username = friend.username,
            name = friend.name,
            onClickUser = { onClickUser(friend.id) },
            onSendRequest = {
                onSendRequest(friend.toRequest())
            },
            onCancelRequest = { onCancelRequest(friend.toRequest()) },
            isFriendRequestSent = isRequestSent,
        )
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
    if (receivedRequests.isNotEmpty()) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.feed_received_requests),
            style = MaterialTheme.typography.titleMedium,
        )

        receivedRequests.forEach {
            ReceivedFriendRequestCard(
                imageUrl = it.imageUrl,
                username = it.name,
                name = it.name,
                onClickUser = { onClickUser(it.userId) },
                onAcceptRequest = { onAcceptRequest(it) },
                onDenyRequest = { onDenyRequest(it) },
            )
        }
    }

    if (friends.isNotEmpty()) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.feed_friends),
            style = MaterialTheme.typography.titleMedium,
        )

        friends.forEach {
            CurrentFriendCard(
                imageUrl = it.imageUrl,
                username = it.username,
                name = it.name,
                onClickUser = { onClickUser(it.id) },
                onRemoveFriend = { onRemoveFriend(it) },
            )
        }
    }

    if (sentRequests.isNotEmpty()) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.feed_sent_requests),
            style = MaterialTheme.typography.titleMedium,
        )

        sentRequests.forEach {
            SentFriendRequestCard(
                imageUrl = it.imageUrl,
                username = it.username,
                name = it.name,
                onClickUser = { onClickUser(it.userId) },
                onCancelRequest = { onCancelRequest(it) },
            )
        }
    }

    if (friends.isEmpty() && receivedRequests.isEmpty() && sentRequests.isEmpty()) {
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
