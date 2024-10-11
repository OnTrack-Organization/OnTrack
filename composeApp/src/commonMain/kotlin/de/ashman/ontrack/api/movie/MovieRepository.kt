package de.ashman.ontrack.api.movie

import de.ashman.ontrack.media.domain.Movie
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.MovieResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.album.safeApiCall
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