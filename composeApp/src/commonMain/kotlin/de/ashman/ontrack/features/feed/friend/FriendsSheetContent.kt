package de.ashman.ontrack.features.feed.friend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.GroupRemove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SearchBar
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_friends
import ontrack.composeapp.generated.resources.feed_no_friends
import ontrack.composeapp.generated.resources.feed_no_potential_friends
import ontrack.composeapp.generated.resources.feed_potential_friends
import ontrack.composeapp.generated.resources.feed_received_requests
import ontrack.composeapp.generated.resources.feed_sent_requests
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsSheetContent(
    uiState: FriendsUiState,
    onRemoveFriend: (Friend) -> Unit,
    onAcceptRequest: (FriendRequest) -> Unit,
    onDenyRequest: (FriendRequest) -> Unit,
    onCancelRequest: (String, FriendRequest) -> Unit,
    onSendRequest: (FriendRequest) -> Unit,
    onClickUser: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
    fetchFriendsAndRequests: () -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        fetchFriendsAndRequests()
    }

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
                    FriendsResultState.Default -> {
                        FriendsAndRequests(
                            receivedRequests = uiState.receivedRequests,
                            sentRequests = uiState.sentRequests,
                            friends = uiState.friends,
                            onAcceptRequest = onAcceptRequest,
                            onDenyRequest = onDenyRequest,
                            onCancelRequest = onCancelRequest,
                            onRemoveFriend = onRemoveFriend,
                            onClickUser = onClickUser,
                        )
                    }

                    FriendsResultState.Success -> {
                        PotentialFriends(
                            potentialFriends = uiState.potentialFriends,
                            onSendRequest = onSendRequest,
                            onClickUser = onClickUser,
                        )
                    }
                    // TODO add other states
                    FriendsResultState.Empty -> {
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

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun PotentialFriends(
    potentialFriends: List<Friend>,
    onSendRequest: (FriendRequest) -> Unit,
    onClickUser: (String) -> Unit,
) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(Res.string.feed_potential_friends),
        style = MaterialTheme.typography.titleMedium,
    )

    potentialFriends.forEach {
        FriendCard(
            imageUrl = it.imageUrl,
            username = it.username,
            name = it.name,
            onClickUser = { onClickUser(it.id) },
            onClickPlus = { onSendRequest(it.toRequest()) },
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
    onCancelRequest: (String, FriendRequest) -> Unit,
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
            FriendCard(
                imageUrl = it.imageUrl,
                username = it.name,
                name = it.name,
                onClickUser = { onClickUser(it.userId) },
                onClickPlus = { onAcceptRequest(it) },
                onClickMinus = { onDenyRequest(it) },
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
            FriendCard(
                imageUrl = it.imageUrl,
                username = it.username,
                name = it.name,
                onClickUser = { onClickUser(it.id) },
                onClickMinus = { onRemoveFriend(it) },
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
            FriendCard(
                imageUrl = it.imageUrl,
                username = it.username,
                name = it.name,
                onClickUser = { onClickUser(it.userId) },
                onClickMinus = { },
            )
        }
    }

    if (friends.isEmpty() && receivedRequests.isEmpty() && sentRequests.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(Res.string.feed_no_friends),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FriendCard(
    imageUrl: String,
    username: String,
    name: String,
    onClickUser: () -> Unit,
    onClickPlus: (() -> Unit)? = null,
    onClickMinus: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickUser() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PersonImage(
                modifier = Modifier.size(42.dp),
                userImageUrl = imageUrl,
                onClick = onClickUser,
            )
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            onClickPlus?.let {
                IconButton(
                    onClick = onClickPlus,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GroupAdd,
                        contentDescription = null,
                    )
                }
            }
            onClickMinus?.let {
                IconButton(
                    onClick = onClickMinus,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GroupRemove,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

fun Friend.toRequest() = FriendRequest(
    userId = id,
    name = name,
    username = username,
    imageUrl = imageUrl,
)