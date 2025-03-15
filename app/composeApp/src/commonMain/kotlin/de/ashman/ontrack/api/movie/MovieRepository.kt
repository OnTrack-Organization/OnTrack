package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.movie.dto.CollectionResponseDto
import de.ashman.ontrack.api.movie.dto.CrewMemberDto
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.MovieResponseDto
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.media.Director
import de.ashman.ontrack.domain.media.Movie
import de.ashman.ontrack.domain.media.MovieCollection
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieRepository(
    private val httpClient: HttpClient,
) : MediaRepository {
    override suspend fun fetchTrending(): Result<List<Movie>> = safeApiCall {
        httpClient.get("trending/movie/week") {
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body<MovieResponseDto>().movies.map { it.toDomain() }
    }

    override suspend fun fetchByQuery(query: String): Result<List<Movie>> = safeApiCall {
        httpClient.get("search/movie") {
            parameter("query", query)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body<MovieResponseDto>().movies.map { it.toDomain() }
    }

    override suspend fun fetchDetails(mediaId: String): Result<Movie> = safeApiCall {
        val response: MovieDto = httpClient.get("movie/$mediaId") {
            parameter("append_to_response", "credits,similar")
        }.body()

        val director = getDirector(response.credits?.crew)
        val collection = fetchMovieCollection(response.belongsToCollection?.id)
        val similarMovies = response.similar?.movies?.map { it.toDomain() }?.takeIf { it.isNotEmpty() }

        response.toDomain().copy(
            similarMovies = similarMovies,
            director = director,
            collection = collection
        )
    }

    private suspend fun fetchMovieCollection(collectionId: Int?): MovieCollection? {
        val response: CollectionResponseDto = httpClient.get("collection/$collectionId").body()
        return response.toDomain()
    }

    private suspend fun getDirector(crew: List<CrewMemberDto>?): Director? {
        val director = crew?.firstOrNull { it.job == "Director" }
        return director?.id?.let {
            val directorResponse: PersonDetailsDto = httpClient.get("person/$it").body()
            directorResponse.toDomain()
        }
    }
}