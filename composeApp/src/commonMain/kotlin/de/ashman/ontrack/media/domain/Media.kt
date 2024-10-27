package de.ashman.ontrack.media.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO add more fields: user rating
@Serializable
sealed class Media {
    abstract val id: String
    abstract val type: MediaType
    abstract val name: String
    abstract val coverUrl: String
    abstract val consumeStatus: StatusType?
    abstract val userRating: Float
}

@Serializable
enum class MediaType {
    @SerialName("album")
    ALBUM,
    @SerialName("boardgame")
    BOARDGAME,
    @SerialName("book")
    BOOK,
    @SerialName("movie")
    MOVIE,
    @SerialName("show")
    SHOW,
    @SerialName("videogame")
    VIDEOGAME,
}

@Serializable
enum class StatusType {
    ALL,
    BINGING,
    PLAYING,
    READING,
    WATCHED,
    BINGED,
    READ,
    PLAYED,
    DROPPED,
    CATALOG;
}