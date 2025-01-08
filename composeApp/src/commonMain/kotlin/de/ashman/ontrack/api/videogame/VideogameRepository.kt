package de.ashman.ontrack.api.videogame

import co.touchlab.kermit.Logger
import de.ashman.ontrack.auth.AccessTokenManager
import de.ashman.ontrack.media.model.Videogame
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
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
) : MediaRepository<Videogame> {

    private val fields =
        """
    id,
    cover.url,
    name
    """.trimIndent()

    private val detailFields =
        """
    id,
    cover.url,
    first_release_date,
    franchises.name,
    franchises.games.name,
    franchises.games.cover.url,
    genres.name,
    name,
    platforms.abbreviation,
    platforms.name,
    platforms.platform_logo.url,
    similar_games.id,
    similar_games.cover.url,
    similar_games.name,
    total_rating,
    total_rating_count,
    summary
    """.trimIndent()

    private suspend fun buildRequestWithToken(
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val token = accessTokenManager.getAccessToken()
        Logger.d("IGDB Token: $token")

        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    override suspend fun fetchMediaByQuery(query: String): Result<List<Videogame>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                //parameter("fields", fields)
                parameter("fields", detailFields)
                parameter("search", query)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.map { it.toDomain() }
        }
    }

    override suspend fun fetchMediaDetails(id: String): Result<Videogame> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                setBody(
                    """
            fields $detailFields;
            where id = $id;
            """.trimIndent()
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            response.first().toDomain()
        }
    }
}
