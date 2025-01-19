package de.ashman.ontrack.domain.sub

import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM
}
