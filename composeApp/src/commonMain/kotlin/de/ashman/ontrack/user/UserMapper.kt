package de.ashman.ontrack.user

import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.db.entity.MediaEntity
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toEntity() =
    UserEntity(
        id = uid,
        email = email.orEmpty(),
        name = displayName.orEmpty(),
        username = displayName.orEmpty(),
        friends = emptyList(),
        media = emptyList()
    )

fun UserEntity.toDomain() =
    User(
        id = id,
        email = email,
        name = name,
        username = username,
        friends = friends,
        media = media.map { it.toDomain() }
    )

// TODO maybe save seperate lists of books, albums etc. to avoid this
fun MediaEntity.toDomain(): Media {
    return when (type) {
        MediaType.MOVIE -> TODO()
        MediaType.SHOW -> TODO()
        MediaType.BOOK -> TODO()
        MediaType.VIDEOGAME -> TODO()
        MediaType.BOARDGAME -> TODO()
        MediaType.ALBUM -> TODO()
    }
}