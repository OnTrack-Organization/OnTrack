package de.ashman.ontrack.api.show

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.movie.dto.CrewMemberDto
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.api.movie.toDomain
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.api.show.dto.ShowResponseDto
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.media.Director
import de.ashman.ontrack.domain.media.Show
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ShowRepository(
    private val httpClient: HttpClient,
) : MediaRepository {
    override suspend fun fetchTrending(): Result<List<Show>> = safeApiCall {
        val response: ShowResponseDto = httpClient.get("trending/tv/week") {
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()
        response.shows.map { it.toDomain() }
    }

    override suspend fun fetchByQuery(query: String): Result<List<Show>> = safeApiCall {
        val response: ShowResponseDto = httpClient.get("search/tv") {
            parameter("query", query)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()
        response.shows.map { it.toDomain() }
    }

    override suspend fun fetchDetails(mediaId: String): Result<Show> = safeApiCall {
        val response: ShowDto = httpClient.get("tv/$mediaId") {
            parameter("append_to_response", "credits,similar")
        }.body()

        val director = getDirector(response.credits?.crew)
        val similarShows = response.similar?.shows?.map { it.toDomain() }

        response.toDomain().copy(
            similarShows = similarShows,
            director = director
        )
    }

    private suspend fun getDirector(crew: List<CrewMemberDto>?): Director? {
        val director = crew?.firstOrNull { it.job == "Director" }
        return director?.id?.let {
            val directorResponse: PersonDetailsDto = httpClient.get("person/$it").body()
            directorResponse.toDomain()
        }
    }
}