package de.ashman.ontrack.api.show

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.api.movie.toDomain
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.api.show.dto.ShowResponseDto
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Show
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ShowRepository(
    private val httpClient: HttpClient,
) : MediaRepository {
    override suspend fun fetchByQuery(query: String): Result<List<Show>> = safeApiCall {
        val response: ShowResponseDto = httpClient.get("search/tv") {
            parameter("query", query)
            parameter("include_adult", false)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()
        response.shows.map { it.toDomain() }
    }

    override suspend fun fetchDetails(media: Media): Result<Show> = safeApiCall {
        val castAppend = "?append_to_response=credits"
        val response: ShowDto = httpClient.get("tv/${media.id}$castAppend").body()

        val director = response.credits?.crew?.firstOrNull { it.job == "Director" }
        val similar = fetchSimilar(media.id)

        val directorDetails = director?.id?.let {
            val directorResponse: PersonDetailsDto = httpClient.get("person/$it").body()
            directorResponse.toDomain()
        }

        response.toDomain().copy(similarShows = similar, director = directorDetails)
    }

    override suspend fun fetchTrending(): Result<List<Show>> = safeApiCall {
        val response: ShowResponseDto = httpClient.get("trending/tv/week") {
            parameter("include_adult", false)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()
        response.shows.map { it.toDomain() }
    }

    private suspend fun fetchSimilar(id: String): List<Show>? {
        val similarResponse: ShowResponseDto = httpClient.get("tv/$id/similar") {
            parameter("include_adult", false)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()

        return similarResponse.shows.takeIf { it.isNotEmpty() }?.map { it.toDomain() }
    }
}