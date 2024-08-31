package de.ashman.ontrack.boardgame.api

import de.ashman.ontrack.boardgame.model.BoardGame
import de.ashman.ontrack.boardgame.model.BoardGameDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// TODO entweder xml akzeptieren hinkriegen oder xml zu json parsen oder json api finden
class BoardGameRepository(
    private val httpClient: HttpClient,
) {
    suspend fun fetchPopular(): Flow<BoardGame?> {
        return flow {
            val response: BoardGameDto = httpClient.get("").body()
            val boardGames = response.toDomain()

            emit(boardGames)
        }
    }
}