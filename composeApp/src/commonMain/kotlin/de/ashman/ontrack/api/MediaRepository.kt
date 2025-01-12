package de.ashman.ontrack.api

import de.ashman.ontrack.media.model.Media

interface MediaRepository {
    suspend fun fetchByQuery(query: String): Result<List<Media>>

    suspend fun fetchDetails(id: String): Result<Media>

    suspend fun fetchTrending(): Result<List<Media>>
}
