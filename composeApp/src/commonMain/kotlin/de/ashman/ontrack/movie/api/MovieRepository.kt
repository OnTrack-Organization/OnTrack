package de.ashman.ontrack.movie.api

import de.ashman.ontrack.movie.model.Movie
import de.ashman.ontrack.movie.model.MovieResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(
    private val httpClient: HttpClient,
) {
    suspend fun fetchPopular(): Flow<List<Movie>?> {
        return flow {
            val response: MovieResponseDto = httpClient.get("movie/popular").body()
            val movies = response.movies?.map { it.toDomain() }

            emit(movies)
        }
    }
}