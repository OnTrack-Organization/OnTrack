package de.ashman.ontrack.api

import de.ashman.ontrack.domain.MediaType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.bgg
import ontrack.composeapp.generated.resources.igdb
import ontrack.composeapp.generated.resources.on_track_icon_v1
import ontrack.composeapp.generated.resources.openlib
import ontrack.composeapp.generated.resources.spotify
import ontrack.composeapp.generated.resources.tmdb
import org.jetbrains.compose.resources.DrawableResource

enum class ApiType(
    val icon: DrawableResource,
    val maxRating: Int,
) {
    TMDB(
        icon = Res.drawable.tmdb,
        maxRating = 10,
    ),
    OpenLibrary(
        icon = Res.drawable.openlib,
        maxRating = 5,
    ),
    IGDB(
        icon = Res.drawable.igdb,
        maxRating = 100,
    ),
    BGG(
        icon = Res.drawable.bgg,
        maxRating = 10,
    ),
    Spotify(
        icon = Res.drawable.spotify,
        maxRating = 100,
    ),
    OnTrack(
        icon = Res.drawable.on_track_icon_v1,
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