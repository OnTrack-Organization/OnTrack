package de.ashman.ontrack.api.videogame

import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class VideogameRepository(
    private val httpClient: HttpClient,
    private val accessTokenManager: AccessTokenManager,
) : MediaRepository {

    private suspend fun buildRequestWithToken(
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val token = accessTokenManager.getAccessToken("IGDB")

        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    override suspend fun fetchByQuery(query: String): Result<List<Videogame>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                setBody(
                """
                    fields cover.url, name;
                    search "$query";
                    limit $DEFAULT_FETCH_LIMIT;
                """
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.map { it.toDomain() }
        }
    }

    override suspend fun fetchDetails(id: String): Result<Videogame> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                setBody(
                """
                    fields cover.url, first_release_date, franchises.name, genres.name, name, platforms.abbreviation, platforms.name, platforms.platform_logo.url, similar_games.cover.url, similar_games.name, total_rating, total_rating_count, summary;
                    where id = $id;
                """
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.first().toDomain()
        }
    }

    override suspend fun fetchTrending(): Result<List<Videogame>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                setBody(
                    """
                    fields cover.url, name;
                    sort first_release_date desc;
                    where total_rating_count > 50 & total_rating > 80;
                    limit $DEFAULT_FETCH_LIMIT;
                """
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.map { it.toDomain() }
        }
    }
}
