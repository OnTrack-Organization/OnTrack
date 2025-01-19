package de.ashman.ontrack.domain

import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM
}
