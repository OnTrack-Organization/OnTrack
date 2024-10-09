package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.MovieDto
import de.ashman.ontrack.media.movie.model.dto.MovieResponseDto
import de.ashman.ontrack.media.MediaRepository
import de.ashman.ontrack.media.album.api.safeApiCall
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