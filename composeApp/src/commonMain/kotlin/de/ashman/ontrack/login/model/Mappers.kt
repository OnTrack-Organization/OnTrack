package de.ashman.ontrack.login.model

import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toDto() =
    UserDto(
        id = uid,
        name = displayName!!,
        email = email!!
    )

fun UserDto.toDomain() =
    User(
        id = id,
        name = name,
        email = email
    )