package de.ashman.ontrack.authentication.user

import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toEntity() =
    UserEntity(
        id = uid,
        email = email,
        displayName = displayName,
        // TODO different way
        username = "@${displayName?.lowercase()}",
        imageUrl = photoURL,
    )

fun UserEntity.toDomain() =
    User(
        id = id,
        email = email,
        name = displayName,
        username = username,
        imageUrl = imageUrl,
        friends = friends,
        //trackings = currentTrackings,
    )
