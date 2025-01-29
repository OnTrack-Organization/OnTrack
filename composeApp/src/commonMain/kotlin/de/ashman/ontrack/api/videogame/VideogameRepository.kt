package de.ashman.ontrack.api.videogame

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.api.videogame.dto.FranchiseDto
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Franchise
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Videogame
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

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

    override suspend fun fetchDetails(media: Media): Result<Videogame> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("games")
                setBody(
                    """
                    fields cover.url, first_release_date, franchises, genres.name, involved_companies.company.name, name, platforms.abbreviation, platforms.name, platforms.platform_logo.url, similar_games.cover.url, similar_games.name, total_rating, total_rating_count, summary;
                    where id = ${media.id};
                """
                )
            }

            val response: List<VideogameDto> = httpClient.post(requestBuilder).body()
            val videogameDto = response.first()

            val franchises = fetchFranchises(franchiseIds = videogameDto.franchises)

            videogameDto.toDomain().copy(franchises = franchises)
        }
    }

    private suspend fun fetchFranchises(franchiseIds: List<Int>?): List<Franchise>? {
        val franchiseIdsString = franchiseIds?.takeIf { it.isNotEmpty() }?.joinToString(",") ?: return null

        val requestBuilder = buildRequestWithToken {
            url("franchises")
            setBody(
            """
                fields *, games.cover.url, games.name;
                where id = ($franchiseIdsString);
            """
            )
        }

        val response: List<FranchiseDto> = httpClient.post(requestBuilder).body()
        return response.takeIf { it.isNotEmpty() }?.map { it.toDomain() }
    }

    // TODO maybe use custom trending again, who knows
    suspend fun fetchTrending2(): Result<List<Videogame>> {
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

    override suspend fun fetchTrending(): Result<List<Videogame>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("popularity_primitives")
                setBody(
                    """
                    fields game_id,value,popularity_type; 
                    sort value desc; 
                    limit $DEFAULT_FETCH_LIMIT; 
                    where popularity_type = 2;
                """
                )
            }

            val response: List<PopularityDto> = httpClient.post(requestBuilder).body()
            val gamesIds = response.map { it.gameId }

            val requestBuilder2 = buildRequestWithToken {
                url("games")
                setBody(
                    """
                        fields cover.url, name;
                        limit $DEFAULT_FETCH_LIMIT; 
                        where id = (${gamesIds.joinToString(",")});
                    """
                )
            }

            val response2: List<VideogameDto> = httpClient.post(requestBuilder2).body()
            response2.map { it.toDomain() }
        }
    }
}

@Serializable
data class PopularityDto(
    val id: Int,
    val gameId: Int,
    val value: Float,
    val popularityType: Int,
)