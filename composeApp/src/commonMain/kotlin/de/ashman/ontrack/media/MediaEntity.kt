package de.ashman.ontrack.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO add all the overlapping fields here and rename domain/entity fields so that they fit
interface MediaEntity {
    val id: String
    val type: MediaType
    val name: String
    val coverUrl: String
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