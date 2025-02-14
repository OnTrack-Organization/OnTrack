package de.ashman.ontrack.domain.user

data class User(
    val id: String,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
    val trackings: List<String> = emptyList(),

    val friends: List<Friend> = emptyList(),
    val receivedRequests: List<FriendRequest> = emptyList(),
    val sentRequests: List<FriendRequest> = emptyList(),
)
