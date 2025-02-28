package de.ashman.ontrack.domain.media

import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM
}
