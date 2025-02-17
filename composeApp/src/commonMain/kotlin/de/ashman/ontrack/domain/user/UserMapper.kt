package de.ashman.ontrack.domain.user

import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain() =
    User(
        id = uid,
        email = email.orEmpty(),
        name = displayName.orEmpty(),
        username = displayName?.lowercase().orEmpty(),
        imageUrl = photoURL.orEmpty(),
    )

fun User.toEntity() = UserEntity(
    id = id,
    email = email,
    displayName = name,
    username = username,
    imageUrl = imageUrl,
)

fun UserEntity.toDomain() =
    User(
        id = id,
        email = email,
        name = displayName,
        username = username,
        imageUrl = imageUrl,
        friends = friends.map { it.toDomain() },
        receivedRequests = receivedRequests.map { it.toDomain() },
        sentRequests = sentRequests.map { it.toDomain() },
    )
