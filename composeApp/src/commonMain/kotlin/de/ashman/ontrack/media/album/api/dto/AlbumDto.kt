package de.ashman.ontrack.media.album.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String,
    val albumType: String,
    val artists: List<ArtistDto>,
    val availableMarkets: List<String>,
    val copyrights: List<CopyrightDto>? = null,
    val externalUrls: ExternalUrlsDto,
    val externalIds: ExternalIdsDto? = null,
    val genres: List<String>? = null,
    val href: String,
    val images: List<ImageDto>,
    val label: String? = null,
    val name: String,
    val popularity: Int? = null,
    val releaseDate: String,
    val releaseDatePrecision: String,
    val restrictions: RestrictionsDto? = null,
    val totalTracks: Int,
    val tracks: TracksDto? = null,
    val type: String,
    val uri: String,
)

@Serializable
data class ExternalUrlsDto(
    val spotify: String
)

@Serializable
data class ImageDto(
    val url: String,
    val height: Int,
    val width: Int
)

@Serializable
data class RestrictionsDto(
    val reason: String
)

@Serializable
data class ArtistDto(
    val externalUrls: ExternalUrlsDto,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

@Serializable
data class TracksDto(
    val href: String,
    val limit: Int,
    val next: String? = null,
    val offset: Int,
    val previous: String? = null,
    val total: Int,
    val items: List<TrackDto>,
)

@Serializable
data class TrackDto(
    val artists: List<ArtistDto>,
    val availableMarkets: List<String>,
    val discNumber: Int,
    val durationMs: Int,
    val explicit: Boolean,
    val externalUrls: ExternalUrlsDto,
    val href: String,
    val id: String,
    val isPlayable: Boolean? = null,
    val linkedFrom: LinkedFromDto? = null,
    val restrictions: RestrictionsDto? = null,
    val name: String,
    val previewUrl: String,
    val trackNumber: Int,
    val type: String,
    val uri: String,
    val isLocal: Boolean
)

@Serializable
data class LinkedFromDto(
    val externalUrls: ExternalUrlsDto,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)

@Serializable
data class CopyrightDto(
    val text: String,
    val type: String
)

@Serializable
data class ExternalIdsDto(
    val isrc: String? = null,
    val ean: String? = null,
    val upc: String?
)
