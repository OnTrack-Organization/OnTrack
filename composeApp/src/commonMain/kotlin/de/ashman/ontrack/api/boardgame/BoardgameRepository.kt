package de.ashman.ontrack.api.boardgame

import de.ashman.ontrack.media.model.Boardgame
import de.ashman.ontrack.api.boardgame.dto.BoardgameResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import nl.adaptivity.xmlutil.serialization.XML

class BoardgameRepository(
    private val httpClient: HttpClient,
) : MediaRepository {

    override suspend fun fetchByQuery(query: String): Result<List<Boardgame>> {
        return safeApiCall {
            val simpleResponse: String = httpClient.get("search") {
                parameter("type", "boardgame")
                parameter("query", query)
            }.body()

            val boardgameIds = convertXmlToResponse(simpleResponse).boardgames.map { it.id }
            if (boardgameIds.isEmpty()) {
                return@safeApiCall emptyList()
            }

            val detailedResponse: String = httpClient.get("thing") {
                parameter("id", boardgameIds.take(DEFAULT_FETCH_LIMIT).joinToString(","))
            }.body()

            convertXmlToResponse(detailedResponse).boardgames.map { it.toDomain() }
        }
    }

    override suspend fun fetchDetails(id: String): Result<Boardgame> {
        return safeApiCall {
            val response: String = httpClient.get("thing") {
                parameter("id", id)
                parameter("stats", 1)
            }.body()

            val boardgame = convertXmlToResponse(response).boardgames.first().toDomain()

            // Fetch details for family items and enrich them with cover URLs
            val familyWithCovers = boardgame.franchiseItems?.map { familyItem ->
                val familyResponse: String = httpClient.get("thing") {
                    parameter("id", familyItem.id)
                }.body()

                val familyDto = convertXmlToResponse(familyResponse).boardgames.firstOrNull()
                familyItem.copy(coverUrl = familyDto?.image.orEmpty())
            }

            boardgame.copy(franchiseItems = familyWithCovers)
        }
    }

    override suspend fun fetchTrending(): Result<List<Boardgame>> {
        return safeApiCall {
            val simpleResponse: String = httpClient.get("hot") {
                parameter("type", "boardgame")
            }.body()

            val boardgameIds = convertXmlToResponse(simpleResponse).boardgames.map { it.id }
            if (boardgameIds.isEmpty()) {
                return@safeApiCall emptyList()
            }

            val detailedResponse: String = httpClient.get("thing") {
                parameter("id", boardgameIds.take(DEFAULT_FETCH_LIMIT).joinToString(","))
            }.body()

            convertXmlToResponse(detailedResponse).boardgames.map { it.toDomain() }
        }
    }

    private fun convertXmlToResponse(xmlString: String): BoardgameResponseDto {
        val xml = XML { indentString = "  " }
        val response = xml.decodeFromString(BoardgameResponseDto.serializer(), xmlString)

        return if (response.boardgames.isEmpty() || response.boardgames.any { it.error != null }) {
            BoardgameResponseDto()
        } else {
            response
        }
    }
}
