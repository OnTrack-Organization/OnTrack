package de.ashman.ontrack.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO add more fields: user rating
interface Media {
    val id: String
    val type: MediaType
    val name: String
    val coverUrl: String
    val consumeStatus: StatusType?
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
    CATALOG,
}