package de.ashman.ontrack.domain.media

import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM;

    companion object {
        fun fromStringOrThrow(value: String?): MediaType {
            return when (value?.lowercase()) {
                "movie" -> MOVIE
                "show" -> SHOW
                "book" -> BOOK
                "videogame" -> VIDEOGAME
                "boardgame" -> BOARDGAME
                "album" -> ALBUM
                else -> throw IllegalArgumentException("Invalid mediaType: $value")
            }
        }
    }
}
