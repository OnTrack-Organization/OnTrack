package de.ashman.ontrack.api

import de.ashman.ontrack.domain.media.Media

interface MediaRepository {
    suspend fun fetchByQuery(query: String): Result<List<Media>>

    suspend fun fetchDetails(mediaId: String): Result<Media>

    suspend fun fetchTrending(): Result<List<Media>>
}
