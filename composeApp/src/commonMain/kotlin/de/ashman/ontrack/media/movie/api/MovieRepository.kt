package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.MovieDto
import de.ashman.ontrack.media.movie.model.dto.MovieResponseDto
import de.ashman.ontrack.xyz.MediaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieRepository(
    private val httpClient: HttpClient,
) : MediaRepository<Movie> {
    suspend fun fetchMoviesByKeyword(keyword: String): List<Movie> {
        val response: MovieResponseDto = httpClient.get("search/movie") {
            parameter("query", keyword)
        }.body()
        return response.movies.map { it.toDomain() }
    }

    override suspend fun fetchMediaDetails(id: Int): Movie {
        val response: MovieDto = httpClient.get("movie/$id").body()
        return response.toDomain()
    }
}