package de.ashman.ontrack.api.videogame

import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.api.videogame.dto.FranchiseDto
import de.ashman.ontrack.api.videogame.dto.PopularityDto
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.media.Franchise
import de.ashman.ontrack.domain.media.Videogame
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

    private suspend inline fun buildRequestWithToken(block: HttpRequestBuilder.() -> Unit): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            val token = accessTokenManager.getAccessToken("IGDB")
            header(HttpHeaders.Authorization, "Bearer $token")
            block()
        }
    }

    override suspend fun fetchTrending(): Result<List<Videogame>> = safeApiCall {
        val response: List<PopularityDto> = httpClient.post(buildRequestWithToken {
            url("popularity_primitives")
            setBody(
                """
                    fields game_id,value,popularity_type; 
                    sort value desc; 
                    limit $DEFAULT_FETCH_LIMIT; 
                    where popularity_type = 2;
                """
            )
        }).body()

        val gamesIds = response.map { it.gameId }

        val response2: List<VideogameDto> = httpClient.post(buildRequestWithToken {
            url("games")
            setBody(
                """
                    fields cover.url, name;
                    limit $DEFAULT_FETCH_LIMIT; 
                    where id = (${gamesIds.joinToString(",")});
                """
            )
        }).body()

        response2.map { it.toDomain() }
    }

    override suspend fun fetchByQuery(query: String): Result<List<Videogame>> = safeApiCall {
        httpClient.post(buildRequestWithToken {
            url("games")
            setBody(
                """
                    fields cover.url, name, total_rating_count;
                    search "$query";
                    limit $DEFAULT_FETCH_LIMIT;
                """
            )
        }).body<List<VideogameDto>>().map { it.toDomain() }
    }

    override suspend fun fetchDetails(mediaId: String): Result<Videogame> = safeApiCall {
        val response: List<VideogameDto> = httpClient.post(buildRequestWithToken {
            url("games")
            setBody(
                """
                    fields cover.url, first_release_date, franchises, genres.name, involved_companies.company.name, name, platforms.abbreviation, platforms.name, platforms.platform_logo.url, similar_games.cover.url, similar_games.name, total_rating, total_rating_count, summary, screenshots.url;
                    where id = ${mediaId};
                """
            )
        }).body()

        Logger.d { "VIDEOGAME RESPONSE $response" }

        val videogame = response.first()
        Logger.d { "VIDEOGAME $videogame" }
        val franchises = fetchFranchises(videogame.franchises)

        videogame.toDomain().copy(franchises = franchises)
    }

    private suspend fun fetchFranchises(franchiseIds: List<Int>?): List<Franchise>? {
        val franchiseIdsString = franchiseIds?.takeIf { it.isNotEmpty() }?.joinToString(",") ?: return null

        val response: List<FranchiseDto> = httpClient.post(buildRequestWithToken {
            url("franchises")
            setBody(
                """
                fields *, games.cover.url, games.name;
                where id = ($franchiseIdsString);
            """
            )
        }).body()

        return response.takeIf { it.isNotEmpty() }?.map { it.toDomain() }
    }
}
