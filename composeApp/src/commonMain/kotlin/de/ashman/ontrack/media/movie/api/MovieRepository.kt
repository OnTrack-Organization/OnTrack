package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.MovieDto
import de.ashman.ontrack.media.movie.model.dto.MovieResponseDto
import de.ashman.ontrack.media.MediaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieRepository(
    private val httpClient: HttpClient,
) : MediaRepository<Movie> {
    override suspend fun fetchMediaByQuery(query: String): List<Movie> {
        val response: MovieResponseDto = httpClient.get("search/movie") {
            parameter("query", query)
        }.body()
        return response.movies.map { it.toDomain() }
    }

    override suspend fun fetchMediaDetails(id: String): Movie {
        val response: MovieDto = httpClient.get("movie/$id").body()
        return response.toDomain()
    }
}