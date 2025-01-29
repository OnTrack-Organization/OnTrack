package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.movie.dto.CollectionResponseDto
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.MovieResponseDto
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.MovieCollection
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieRepository(
    private val httpClient: HttpClient,
) : MediaRepository {
    override suspend fun fetchByQuery(query: String): Result<List<Movie>> = safeApiCall {
        val response: MovieResponseDto = httpClient.get("search/movie") {
            parameter("query", query)
            parameter("include_adult", false)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()
        response.movies.map { it.toDomain() }
    }

    override suspend fun fetchDetails(media: Media): Result<Movie> = safeApiCall {
        val castAppend = "?append_to_response=credits"
        val response: MovieDto = httpClient.get("movie/${media.id}$castAppend").body()

        // TODO maybe do director and producer or similar
        val director = response.credits?.crew?.firstOrNull { it.job == "Director" }
        val directorDetails = director?.id?.let {
            val directorResponse: PersonDetailsDto = httpClient.get("person/$it").body()
            directorResponse.toDomain()
        }
        val collection = fetchMovieCollection(response.belongsToCollection?.id)
        val similar = fetchSimilar(media.id)

        response.toDomain().copy(similarMovies = similar, director = directorDetails, collection = collection)
    }

    override suspend fun fetchTrending(): Result<List<Movie>> = safeApiCall {
        val response: MovieResponseDto = httpClient.get("trending/movie/week") {
            parameter("include_adult", false)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()
        response.movies.map { it.toDomain() }
    }

    private suspend fun fetchSimilar(id: String): List<Movie>? {
        val similarResponse: MovieResponseDto = httpClient.get("movie/$id/similar") {
            parameter("include_adult", false)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()

        return similarResponse.movies.takeIf { it.isNotEmpty() }?.map { it.toDomain() }
    }

    private suspend fun fetchMovieCollection(collectionId: Int?): MovieCollection {
        val response: CollectionResponseDto = httpClient.get("collection/$collectionId").body()
        return response.toDomain()
    }
}