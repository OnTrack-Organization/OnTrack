package de.ashman.ontrack.api.boardgame

import co.touchlab.kermit.Logger
import de.ashman.ontrack.media.model.Boardgame
import de.ashman.ontrack.api.boardgame.dto.BoardgameResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.media.model.Media
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

            Logger.i(response)

            convertXmlToResponse(response).boardgames.first().toDomain()
        }
    }

    override suspend fun fetchTrending(): Result<List<Media>> {
        return safeApiCall {
            val simpleResponse: String = httpClient.get("hot") {
                parameter("type", "boardgame")
            }.body()

            val boardgameIds = convertXmlToResponse(simpleResponse).boardgames.map { it.id }

            val detailedResponse: String = httpClient.get("thing") {
                parameter("id", boardgameIds.take(DEFAULT_FETCH_LIMIT).joinToString(","))
            }.body()

            convertXmlToResponse(detailedResponse).boardgames.map { it.toDomain() }
        }
    }

    private fun convertXmlToResponse(xmlString: String): BoardgameResponseDto {
        val xml = XML { indentString = "  " }
        return xml.decodeFromString(BoardgameResponseDto.serializer(), xmlString)
    }
}