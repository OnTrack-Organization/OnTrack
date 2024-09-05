package de.ashman.ontrack.boardgame.api

import de.ashman.ontrack.boardgame.model.BoardGame
import de.ashman.ontrack.boardgame.model.BoardGamesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nl.adaptivity.xmlutil.serialization.XML

class BoardGameRepository(
    private val httpClient: HttpClient,
) {
    suspend fun fetchBoardGame(): Flow<List<BoardGame>?> {
        return flow {
            val response = httpClient.get("thing?id=358661").body<String>()

            val bg = convertBoardGameXmlToJson(response).boardGames?.map { it.toDomain() }
            emit(bg)
        }
    }

    private fun convertBoardGameXmlToJson(xmlString: String): BoardGamesResponse {
        val xml = XML { indentString = "  " }

        val boardGame: BoardGamesResponse = xml.decodeFromString(BoardGamesResponse.serializer(), xmlString)

        return boardGame
    }
}