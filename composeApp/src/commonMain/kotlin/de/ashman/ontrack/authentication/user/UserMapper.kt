package de.ashman.ontrack.authentication.user

import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toEntity() =
    UserEntity(
        id = uid,
        email = email,
        name = displayName,
        username = displayName,
    )

fun UserEntity.toDomain() =
    User(
        id = id,
        email = email,
        name = name,
        username = username,
    )
