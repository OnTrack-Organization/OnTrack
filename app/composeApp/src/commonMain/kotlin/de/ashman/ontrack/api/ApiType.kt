package de.ashman.ontrack.api

import de.ashman.ontrack.domain.media.MediaType
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.app_icon
import ontrack.app.composeapp.generated.resources.bgg
import ontrack.app.composeapp.generated.resources.igdb
import ontrack.app.composeapp.generated.resources.openlib
import ontrack.app.composeapp.generated.resources.spotify
import ontrack.app.composeapp.generated.resources.tmdb
import org.jetbrains.compose.resources.DrawableResource

enum class ApiType(
    val icon: DrawableResource,
    val maxRating: Int,
    val uri: String? = null,
) {
    TMDB(
        icon = Res.drawable.tmdb,
        maxRating = 10,
        uri = "https://www.themoviedb.org/",
    ),
    OpenLibrary(
        icon = Res.drawable.openlib,
        maxRating = 5,
        uri = "https://openlibrary.org/",
    ),
    IGDB(
        icon = Res.drawable.igdb,
        maxRating = 100,
        uri = "https://www.igdb.com/",
    ),
    BGG(
        icon = Res.drawable.bgg,
        maxRating = 10,
        uri = "https://boardgamegeek.com/",
    ),
    Spotify(
        icon = Res.drawable.spotify,
        maxRating = 100,
        uri = "https://open.spotify.com/",
    ),
    OnTrack(
        icon = Res.drawable.app_icon,
        maxRating = 5,
    ),
}

fun MediaType.getRatingType(): ApiType {
    return when (this) {
        MediaType.MOVIE -> return ApiType.TMDB
        MediaType.SHOW -> return ApiType.TMDB
        MediaType.BOOK -> return ApiType.OpenLibrary
        MediaType.ALBUM -> return ApiType.Spotify
        MediaType.VIDEOGAME -> return ApiType.IGDB
        MediaType.BOARDGAME -> return ApiType.BGG
    }
}