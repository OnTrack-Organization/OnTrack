package de.ashman.ontrack.api.boardgame

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.boardgame.dto.BoardgameResponseDto
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.BoardgameDesigner
import de.ashman.ontrack.domain.Media
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.collections.contains

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

    override suspend fun fetchDetails(media: Media): Result<Boardgame> {
        return safeApiCall {
            val response: String = httpClient.get("thing") {
                parameter("id", media.id)
                parameter("stats", 1)
            }.body()

            val boardgame = convertXmlToResponse(response).boardgames.first().toDomain()
            val designer = scrapeDesigner(boardgame.designer?.id).getOrNull()

            val franchiseItems = boardgame.franchise
                ?.filter { it.boardgameType in setOf("boardgameimplementation", "boardgameexpansion", "boardgameintegration", "boardgamecompilation") }
                ?.take(10)
                ?.map {
                    val franchiseResponse: String = httpClient.get("thing") {
                        parameter("id", it.id)
                    }.body()

                    val boardgameDto = convertXmlToResponse(franchiseResponse).boardgames.firstOrNull()
                    it.copy(coverUrl = boardgameDto?.image.orEmpty())
                }
                ?.takeIf { it.isNotEmpty() }

            boardgame.copy(franchise = franchiseItems, designer = designer)
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

    suspend fun scrapeDesigner(id: String?): Result<BoardgameDesigner> {
        return safeApiCall {
            val url = "https://boardgamegeek.com/boardgamedesigner/$id"
            val document: Document = Ksoup.parseGetRequest(url)

            val name = document.select("meta[name=title]").attr("content")
            val imageUrl = document.select("link[rel=preload][as=image]").firstOrNull()?.attr("href")
            val bio = document.select("meta[name=description]").attr("content")

            BoardgameDesigner(id = id.orEmpty(), name = name, imageUrl = imageUrl, bio = bio)
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
