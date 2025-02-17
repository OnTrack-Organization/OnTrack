package de.ashman.ontrack.domain.user

import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain() =
    User(
        id = uid,
        fcmToken = "",
        email = email.orEmpty(),
        name = displayName.orEmpty(),
        username = displayName?.lowercase().orEmpty(),
        imageUrl = photoURL.orEmpty(),
    )

fun User.toEntity() = UserEntity(
    id = id,
    fcmToken = fcmToken,
    email = email,
    displayName = name,
    username = username,
    imageUrl = imageUrl,
)

fun UserEntity.toDomain() =
    User(
        id = id,
        fcmToken = fcmToken,
        email = email,
        name = displayName,
        username = username,
        imageUrl = imageUrl,
    )
