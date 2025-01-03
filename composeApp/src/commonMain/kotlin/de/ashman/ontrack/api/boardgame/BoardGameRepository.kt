package de.ashman.ontrack.api.boardgame

import de.ashman.ontrack.media.domain.BoardGame
import de.ashman.ontrack.api.boardgame.dto.BoardGameResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.album.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
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
                parameter("id", boardgameIds.take(DEFAULT_FETCH_LIMIT).joinToString(","))
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