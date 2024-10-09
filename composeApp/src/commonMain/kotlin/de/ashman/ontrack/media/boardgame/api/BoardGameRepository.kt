package de.ashman.ontrack.media.boardgame.api

import de.ashman.ontrack.media.boardgame.model.domain.BoardGame
import de.ashman.ontrack.media.boardgame.model.dto.BoardGameResponseDto
import de.ashman.ontrack.media.MediaRepository
import de.ashman.ontrack.media.album.api.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import nl.adaptivity.xmlutil.serialization.XML

class BoardGameRepository(
    private val httpClient: HttpClient,
) : MediaRepository<BoardGame> {

    override suspend fun fetchMediaByQuery(query: String): Result<List<BoardGame>> {
        return safeApiCall {
            val simpleResponse: String = httpClient.get("search") {
                parameter("type", "boardgame")
                parameter("query", query)
            }.body()

            val boardgameIds = convertXmlToResponse(simpleResponse).boardGames.map { it.id }

            val detailedResponse: String = httpClient.get("thing") {
                // TODO only the first 20 are possible all at once. maybe get 5 or 10 and show more on scroll... or do more fetches immediately
                parameter("id", boardgameIds.take(3).joinToString(","))
            }.body()

            convertXmlToResponse(detailedResponse).boardGames.map { it.toDomain() }
        }
    }

    override suspend fun fetchMediaDetails(id: String): Result<BoardGame> {
        return safeApiCall {
            val response: String = httpClient.get("thing") {
                parameter("id", id)
                parameter("stats", 1)
            }.body()

            convertXmlToResponse(response).boardGames.first().toDomain()
        }
    }

    private fun convertXmlToResponse(xmlString: String): BoardGameResponseDto {
        val xml = XML { indentString = "  " }
        return xml.decodeFromString(BoardGameResponseDto.serializer(), xmlString)
    }
}