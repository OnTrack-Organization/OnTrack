package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.MovieResponseDto
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieRepository(
    private val httpClient: HttpClient,
) : MediaRepository {
    override suspend fun fetchByQuery(query: String): Result<List<Movie>> {
        return safeApiCall {
            val response: MovieResponseDto = httpClient.get("search/movie") {
                parameter("query", query)
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()
            response.movies.map { it.toDomain() }
        }
    }

    override suspend fun fetchDetails(id: String): Result<Movie> {
        return safeApiCall {
            val response: MovieDto = httpClient.get("movie/$id").body()

            val similar = fetchSimilar(id).getOrNull()?.takeIf { it.isNotEmpty() }

            response.toDomain().copy(similarMovies = similar)
        }
    }

    override suspend fun fetchTrending(): Result<List<Movie>> {
        return safeApiCall {
            val response: MovieResponseDto = httpClient.get("trending/movie/week") {
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()
            response.movies.map { it.toDomain() }
        }
    }

    suspend fun fetchSimilar(id: String): Result<List<Movie>> {
        return safeApiCall {
            val similarResponse: MovieResponseDto = httpClient.get("movie/$id/similar") {
                parameter("include_adult", false)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            similarResponse.movies.map { it.toDomain() }
        }
    }
}