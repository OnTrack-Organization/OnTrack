package de.ashman.ontrack.api.album.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val externalUrls: ExternalUrlsDto,
    val followers: FollowersDto? = null,
    val genres: List<String>? = null,
    val href: String,
    val id: String,
    val images: List<ImageDto>? = null,
    val name: String,
    val popularity: Int? = null,
    val type: String,
    val uri: String
)

@Serializable
data class FollowersDto(
    val href: String? = null,
    val total: Int
)
