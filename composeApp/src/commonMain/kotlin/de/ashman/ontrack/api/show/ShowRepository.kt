package de.ashman.ontrack.api.show

import de.ashman.ontrack.media.model.Show
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.api.show.dto.ShowResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.movie.dto.MovieResponseDto
import de.ashman.ontrack.api.movie.toDomain
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.media.model.Media
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
            response.toDomain()
        }
    }

    override suspend fun fetchTrending(): Result<List<Media>> {
        return safeApiCall {
            val response: ShowResponseDto = httpClient.get("trending/tv/week") {
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()
            response.shows.map { it.toDomain() }
        }
    }
}