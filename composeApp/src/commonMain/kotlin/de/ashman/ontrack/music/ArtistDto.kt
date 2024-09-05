package de.ashman.ontrack.music

import kotlinx.serialization.Serializable

@Serializable
class ArtistDto(
    val externalUrls: ExternalUrlsDto,
    val followers: FollowersDto,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<ImageDto>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

@Serializable
data class ExternalUrlsDto(
    val spotify: String
)

@Serializable
data class FollowersDto(
    val href: String?,
    val total: Int
)

@Serializable
data class ImageDto(
    val height: Int,
    val url: String,
    val width: Int
)