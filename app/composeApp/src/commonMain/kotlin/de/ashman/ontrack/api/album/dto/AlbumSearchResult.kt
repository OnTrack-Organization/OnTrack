package de.ashman.ontrack.api.album.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumSearchResult(
    val albums: AlbumResponseDto,
)

@Serializable
data class AlbumResponseDto(
    val href: String,
    val items: List<AlbumDto>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int,
)