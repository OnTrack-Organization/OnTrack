package de.ashman.ontrack.api.videogame

import co.touchlab.kermit.Logger
import de.ashman.ontrack.auth.AccessTokenManager
import de.ashman.ontrack.media.model.Videogame
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.media.model.Media
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
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
                setBody("id, cover.url, first_release_date, franchises.name, franchises.games.name, franchises.games.cover.url, genres.name, name, platforms.abbreviation, platforms.name, platforms.platform_logo.url, similar_games.id, similar_games.cover.url, similar_games.name, total_rating, total_rating_count, summary;")
                parameter("search", query)
                parameter("limit", DEFAULT_FETCH_LIMIT)
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
                    fields id, cover.url, first_release_date, franchises.name, franchises.games.name, franchises.games.cover.url, genres.name, name, platforms.abbreviation, platforms.name, platforms.platform_logo.url, similar_games.id, similar_games.cover.url, similar_games.name, total_rating, total_rating_count, summary;
                    where id = $id;
                """.trimIndent()
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.first().toDomain()
        }
    }

    override suspend fun fetchTrending(): Result<List<Media>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                setBody(
                    """
                    fields id, cover.url, first_release_date, name, total_rating, total_rating_count;
                    sort first_release_date desc;
                    limit $DEFAULT_FETCH_LIMIT;
                    where total_rating_count > 50 & total_rating > 80;
                """.trimIndent()
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.map { it.toDomain() }
        }
    }
}
