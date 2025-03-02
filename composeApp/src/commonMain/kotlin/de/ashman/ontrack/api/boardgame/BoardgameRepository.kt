package de.ashman.ontrack.api.boardgame

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.utils.convertXmlToResponse
import de.ashman.ontrack.api.utils.decodeHtmlManually
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.media.Boardgame
import de.ashman.ontrack.domain.media.BoardgameDesigner
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlin.collections.contains

class BoardgameRepository(
    private val httpClient: HttpClient,
) : MediaRepository {

    override suspend fun fetchByQuery(query: String): Result<List<Boardgame>> = safeApiCall {
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

    override suspend fun fetchDetails(mediaId: String): Result<Boardgame> = safeApiCall {
        val response: String = httpClient.get("thing") {
            parameter("id", mediaId)
            parameter("stats", 1)
        }.body()

        val boardgame = convertXmlToResponse(response).boardgames.first().toDomain()
        val designer = scrapeDesigner(boardgame.designer?.id)

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

    override suspend fun fetchTrending(): Result<List<Boardgame>> = safeApiCall {
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

    private suspend fun scrapeDesigner(id: String?): BoardgameDesigner {
        val document: Document = Ksoup.parseGetRequest("https://boardgamegeek.com/boardgamedesigner/$id")

        return BoardgameDesigner(
            id = id.orEmpty(),
            name = document.select("meta[name=title]").attr("content"),
            imageUrl = document.select("link[rel=preload][as=image]").firstOrNull()?.attr("href"),
            bio = document.select("meta[name=description]").attr("content").decodeHtmlManually()
        )
    }
}
