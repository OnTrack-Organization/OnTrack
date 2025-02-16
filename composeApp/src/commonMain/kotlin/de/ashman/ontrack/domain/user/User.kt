package de.ashman.ontrack.domain.user

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val imageUrl: String,
    val trackings: List<String> = emptyList(),

    val friends: List<Friend> = emptyList(),
    val receivedRequests: List<FriendRequest> = emptyList(),
    val sentRequests: List<FriendRequest> = emptyList(),
)
