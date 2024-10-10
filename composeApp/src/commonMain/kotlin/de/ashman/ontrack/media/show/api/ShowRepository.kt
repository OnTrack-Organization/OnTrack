package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.model.Show
import de.ashman.ontrack.media.show.model.dto.ShowDto
import de.ashman.ontrack.media.show.model.dto.ShowResponseDto
import de.ashman.ontrack.media.MediaRepository
import de.ashman.ontrack.media.album.api.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ShowRepository(
    private val httpClient: HttpClient,
) : MediaRepository<Show> {
    override suspend fun fetchMediaByQuery(query: String): Result<List<Show>> {
        return safeApiCall {
            val response: ShowResponseDto = httpClient.get("search/tv") {
                parameter("query", query)
            }.body()
            response.shows.map { it.toDomain() }
        }
    }

    override suspend fun fetchMediaDetails(id: String): Result<Show> {
        return safeApiCall {
            val response: ShowDto = httpClient.get("tv/$id").body()
            response.toDomain()
        }
    }
}