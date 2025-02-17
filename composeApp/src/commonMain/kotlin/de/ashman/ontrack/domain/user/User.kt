package de.ashman.ontrack.domain.user

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val imageUrl: String,
)
