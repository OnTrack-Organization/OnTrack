package de.ashman.ontrack.features.friend

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.domain.user.OtherUser
import de.ashman.ontrack.features.common.SearchBar
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_friends
import ontrack.composeapp.generated.resources.feed_no_friends_and_requests
import ontrack.composeapp.generated.resources.feed_no_potential_friends
import ontrack.composeapp.generated.resources.feed_received_requests
import ontrack.composeapp.generated.resources.feed_sent_requests
import ontrack.composeapp.generated.resources.friends_get_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsSheet(
    uiState: FriendsUiState,
    fetchFriendsAndRequests: () -> Unit,
    onSendRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onClickUser: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
) {
    var showFriendRemoveConfirmDialog by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf<OtherUser?>(null) }

    val localFocusManager = LocalFocusManager.current

    val friends = uiState.users.filter { it.friendStatus == FriendStatus.FRIEND }
    val receivedRequests = uiState.users.filter { it.friendStatus == FriendStatus.REQUEST_RECEIVED }
    val sentRequests = uiState.users.filter { it.friendStatus == FriendStatus.REQUEST_SENT }
    val queriedUsers = uiState.users.filter { it.friendStatus == FriendStatus.STRANGER }

    LaunchedEffect(Unit) {
        fetchFriendsAndRequests()
    }

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
                FriendsResultState.Success -> {
                    FriendsAndRequests(
                        receivedRequests = receivedRequests,
                        sentRequests = sentRequests,
                        friends = friends,
                        onAccept = onAcceptRequest,
                        onDecline = onDeclineRequest,
                        onCancel = onCancelRequest,
                        onRemove = {
                            selectedFriend = it
                            showFriendRemoveConfirmDialog = true
                        },
                        onClickUser = onClickUser,
                    )
                }

                FriendsResultState.Empty -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(Res.string.feed_no_friends_and_requests),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FriendsResultState.QuerySuccess -> {
                    QueriedUsers(
                        users = queriedUsers,
                        onCancel = onCancelRequest,
                        onSend = onSendRequest,
                        onClickUser = onClickUser,
                    )
                }

                FriendsResultState.QueryEmpty -> {
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

                FriendsResultState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                FriendsResultState.Error -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(Res.string.friends_get_error),
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
                    selectedFriend?.let {
                        onRemoveFriend(it.user.id)
                        showFriendRemoveConfirmDialog = false
                    }
                },
                onDismiss = { showFriendRemoveConfirmDialog = false },
            )
        }
    }
}

@Composable
fun QueriedUsers(
    users: List<OtherUser>,
    onSend: (String) -> Unit,
    onCancel: (String) -> Unit,
    onClickUser: (String) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            FriendCard(
                profilePictureUrl = user.user.profilePictureUrl,
                username = user.user.username,
                name = user.user.name,
                friendStatus = user.friendStatus,
                onClickUser = { onClickUser(user.user.id) },
                onSendRequest = { onSend(user.user.id) },
                onCancelRequest = { onCancel(user.user.id) },
            )
        }
    }
}

@Composable
fun FriendsAndRequests(
    receivedRequests: List<OtherUser>,
    sentRequests: List<OtherUser>,
    friends: List<OtherUser>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (OtherUser) -> Unit,
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

            items(receivedRequests) { request ->
                FriendCard(
                    profilePictureUrl = request.user.profilePictureUrl,
                    username = request.user.username,
                    name = request.user.name,
                    friendStatus = request.friendStatus,
                    onClickUser = { onClickUser(request.user.id) },
                    onAcceptRequest = { onAccept(request.user.id) },
                    onDenyRequest = { onDecline(request.user.id) },
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

            items(friends) { friend ->
                FriendCard(
                    profilePictureUrl = friend.user.profilePictureUrl,
                    username = friend.user.username,
                    name = friend.user.name,
                    friendStatus = friend.friendStatus,
                    onClickUser = { onClickUser(friend.user.id) },
                    onRemoveFriend = { onRemove(friend) },
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

            items(sentRequests) { request ->
                FriendCard(
                    profilePictureUrl = request.user.profilePictureUrl,
                    username = request.user.username,
                    name = request.user.name,
                    friendStatus = request.friendStatus,
                    onClickUser = { onClickUser(request.user.id) },
                    onCancelRequest = { onCancel(request.user.id) },
                )
            }
        }

        if (friends.isEmpty() && receivedRequests.isEmpty() && sentRequests.isEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(Res.string.feed_no_friends_and_requests),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

