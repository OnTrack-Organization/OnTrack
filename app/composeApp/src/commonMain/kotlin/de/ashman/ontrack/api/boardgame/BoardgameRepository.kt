package de.ashman.ontrack.api.boardgame

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.boardgame.dto.BoardgameImagesDto
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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class BoardgameRepository(
    private val bggClient: HttpClient,
    private val geekDoClient: HttpClient,
) : MediaRepository {

    override suspend fun fetchTrending(): Result<List<Boardgame>> = safeApiCall {
        val boardgameIds = fetchBoardgameIds("hot")
        if (boardgameIds.isEmpty()) return@safeApiCall emptyList()

        fetchBoardgamesByIds(boardgameIds)
    }

    override suspend fun fetchByQuery(query: String): Result<List<Boardgame>> = safeApiCall {
        val boardgameIds = fetchBoardgameIds("search", query)
        if (boardgameIds.isEmpty()) return@safeApiCall emptyList()

        fetchBoardgamesByIds(boardgameIds)
    }

    override suspend fun fetchDetails(mediaId: String): Result<Boardgame> = safeApiCall {
        val response: String = bggClient.get("thing") {
            parameter("id", mediaId)
            parameter("stats", 1)
        }.body()

        val boardgame = convertXmlToResponse(response).boardgames.first().toDomain()

        coroutineScope {
            val designerDeferred = async { boardgame.designer?.let { scrapeDesigner(it.id) } }
            val franchiseItemsDeferred = async { fetchFranchiseItems(boardgame.franchise) }
            val imagesDeferred = async { fetchImages(boardgame.id) }

            val designer = designerDeferred.await()
            val franchiseItems = franchiseItemsDeferred.await()
            val images = imagesDeferred.await()

            boardgame.copy(
                franchise = franchiseItems,
                designer = designer,
                images = images,
            )
        }
    }

    private suspend fun fetchFranchiseItems(franchise: List<Boardgame>?): List<Boardgame>? {
        return franchise?.filter { it.boardgameType in setOf("boardgameimplementation", "boardgameexpansion", "boardgameintegration", "boardgamecompilation") }
            ?.take(10)
            ?.map { fetchFranchiseCoverUrl(it) }
            ?.takeIf { it.isNotEmpty() }
    }

    private suspend fun fetchFranchiseCoverUrl(boardgameItem: Boardgame): Boardgame {
        val franchiseResponse: String = bggClient.get("thing") {
            parameter("id", boardgameItem.id)
        }.body()

        val boardgameDto = convertXmlToResponse(franchiseResponse).boardgames.firstOrNull()
        return boardgameItem.copy(coverUrl = boardgameDto?.image.orEmpty())
    }

    private suspend fun fetchBoardgameIds(type: String, query: String? = null): List<String> {
        val response: String = bggClient.get(type) {
            parameter("type", "boardgame")
            query?.let { parameter("query", it) }
        }.body()

        return convertXmlToResponse(response).boardgames.mapNotNull { it.id }
    }

    private suspend fun fetchBoardgamesByIds(bgIds: List<String>): List<Boardgame> {
        val response: String = bggClient.get("thing") {
            parameter("id", bgIds.take(DEFAULT_FETCH_LIMIT).joinToString(","))
            parameter("stats", 1)
        }.body()

        return convertXmlToResponse(response).boardgames.map { it.toDomain() }
    }

    private suspend fun scrapeDesigner(id: String): BoardgameDesigner {
        val document: Document = Ksoup.parseGetRequest("https://boardgamegeek.com/boardgamedesigner/$id")

        return BoardgameDesigner(
            id = id,
            name = document.select("meta[name=title]").attr("content"),
            imageUrl = document.select("link[rel=preload][as=image]").firstOrNull()?.attr("href"),
            bio = document.select("meta[name=description]").attr("content").decodeHtmlManually()
        )
    }

    private suspend fun fetchImages(bgId: String): List<String> =
        geekDoClient.get("images") {
            parameter("objectid", bgId)
            parameter("objecttype", "thing")
        }.body<BoardgameImagesDto>().images.take(10).map { it.imageUrlLarge }

}
