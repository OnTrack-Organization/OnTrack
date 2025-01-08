package de.ashman.ontrack.api.movie

import de.ashman.ontrack.media.model.Movie
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.MovieResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieRepository(
    private val httpClient: HttpClient,
) : MediaRepository<Movie> {
    override suspend fun fetchMediaByQuery(query: String): Result<List<Movie>> {
        return safeApiCall {
            val response: MovieResponseDto = httpClient.get("search/movie") {
                parameter("query", query)
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()
            response.movies.map { it.toDomain() }
        }
    }

    override suspend fun fetchMediaDetails(id: String): Result<Movie> {
        return safeApiCall {
            val response: MovieDto = httpClient.get("movie/$id").body()
            response.toDomain()
        }
    }
}