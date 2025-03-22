package de.ashman.ontrack.api

import de.ashman.ontrack.domain.media.MediaType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.api_bgg
import ontrack.composeapp.generated.resources.api_igdb
import ontrack.composeapp.generated.resources.api_openlib
import ontrack.composeapp.generated.resources.api_spotify
import ontrack.composeapp.generated.resources.api_tmdb
import ontrack.composeapp.generated.resources.app_icon
import ontrack.composeapp.generated.resources.bgg
import ontrack.composeapp.generated.resources.igdb
import ontrack.composeapp.generated.resources.openlib
import ontrack.composeapp.generated.resources.spotify
import ontrack.composeapp.generated.resources.tmdb
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class ApiType(
    val title: StringResource,
    val icon: DrawableResource,
    val maxRating: Int,
    val websiteUrl: String? = null,
) {
    TMDB(
        title = Res.string.api_tmdb,
        icon = Res.drawable.tmdb,
        maxRating = 10,
        websiteUrl = "https://www.themoviedb.org/",
    ),
    OpenLibrary(
        title = Res.string.api_openlib,
        icon = Res.drawable.openlib,
        maxRating = 5,
        websiteUrl = "https://openlibrary.org/",
    ),
    IGDB(
        title = Res.string.api_igdb,
        icon = Res.drawable.igdb,
        maxRating = 100,
        websiteUrl = "https://www.igdb.com/",
    ),
    BGG(
        title = Res.string.api_bgg,
        icon = Res.drawable.bgg,
        maxRating = 10,
        websiteUrl = "https://boardgamegeek.com/",
    ),
    Spotify(
        title = Res.string.api_spotify,
        icon = Res.drawable.spotify,
        maxRating = 100,
        websiteUrl = "https://open.spotify.com/",
    ),
}

enum class OnTrackApiType(
    val icon: DrawableResource,
    val maxRating: Int,
) {
    OnTrack(
        icon = Res.drawable.app_icon,
        maxRating = 5,
    )
}

fun MediaType.getApiType(): ApiType {
    return when (this) {
        MediaType.MOVIE, MediaType.SHOW -> ApiType.TMDB
        MediaType.BOOK -> ApiType.OpenLibrary
        MediaType.ALBUM -> ApiType.Spotify
        MediaType.VIDEOGAME -> ApiType.IGDB
        MediaType.BOARDGAME -> ApiType.BGG
    }
}