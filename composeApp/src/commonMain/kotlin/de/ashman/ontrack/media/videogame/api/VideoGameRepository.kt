package de.ashman.ontrack.media.videogame.api

import de.ashman.ontrack.auth.AccessTokenManager
import de.ashman.ontrack.media.videogame.model.domain.VideoGame
import de.ashman.ontrack.media.videogame.model.dto.VideoGameDto
import de.ashman.ontrack.xyz.MediaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class VideoGameRepository(
    private val httpClient: HttpClient,
    private val accessTokenManager: AccessTokenManager,
) : MediaRepository<VideoGame> {

    // Some APIS need field selection to reduce traffic
    // fields * um alle zu holen
    private val fields =
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
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    override suspend fun fetchMediaByKeyword(keyword: String): List<VideoGame> {
        val requestBuilder = buildRequestWithToken {
            url("games")
            parameter("fields", fields)
            parameter("search", keyword)
        }

        val response: List<VideoGameDto> = httpClient.post(requestBuilder).body()
        return response.map { it.toDomain() }
    }

    override suspend fun fetchMediaDetails(id: String): VideoGame {
        val requestBuilder = buildRequestWithToken {
            url("games")
            setBody(
                """
            fields $fields;
            where id = $id;
            """.trimIndent()
            )
        }

        val response: List<VideoGameDto> = httpClient.post(requestBuilder).body()
        return response.first().toDomain()
    }
}
