package de.ashman.ontrack.authentication.user

data class User(
    val id: String,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
    val friends: List<String> = emptyList(),
    val trackings: List<String> = emptyList(),
)
