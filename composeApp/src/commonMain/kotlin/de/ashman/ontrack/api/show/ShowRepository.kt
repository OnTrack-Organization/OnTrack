package de.ashman.ontrack.api.show

import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.api.show.dto.ShowResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ShowRepository(
    private val httpClient: HttpClient,
) : MediaRepository {
    override suspend fun fetchByQuery(query: String): Result<List<Show>> {
        return safeApiCall {
            val response: ShowResponseDto = httpClient.get("search/tv") {
                parameter("query", query)
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()
            response.shows.map { it.toDomain() }
        }
    }

    override suspend fun fetchDetails(id: String): Result<Show> {
        return safeApiCall {
            val response: ShowDto = httpClient.get("tv/$id").body()

            val similar = fetchSimilar(id).getOrNull()

            response.toDomain().copy(similarShows = similar)
        }
    }

    override suspend fun fetchTrending(): Result<List<Show>> {
        return safeApiCall {
            val response: ShowResponseDto = httpClient.get("trending/tv/week") {
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()
            response.shows.map { it.toDomain() }
        }
    }

    suspend fun fetchSimilar(id: String): Result<List<Show>> {
        return safeApiCall {
            val similarResponse: ShowResponseDto = httpClient.get("tv/$id/similar") {
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            similarResponse.shows.map { it.toDomain() }
        }
    }
}