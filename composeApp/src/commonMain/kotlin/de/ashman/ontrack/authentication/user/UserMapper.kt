package de.ashman.ontrack.authentication.user

import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toEntity() =
    UserEntity(
        id = uid,
        email = email,
        name = displayName,
        // TODO different way
        username = "@${displayName?.lowercase()}",
        imageUrl = photoURL,
    )

fun UserEntity.toDomain() =
    User(
        id = id,
        email = email,
        name = name,
        username = username,
        imageUrl = imageUrl,
    )
