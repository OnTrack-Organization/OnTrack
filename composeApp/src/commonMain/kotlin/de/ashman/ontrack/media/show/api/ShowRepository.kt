package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.show.model.domain.Show
import de.ashman.ontrack.media.show.model.dto.ShowDto
import de.ashman.ontrack.media.show.model.dto.ShowResponseDto
import de.ashman.ontrack.xyz.MediaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ShowRepository(
    private val httpClient: HttpClient,
) : MediaRepository<Show> {
    override suspend fun fetchMediaByKeyword(keyword: String): List<Show> {
        val response: ShowResponseDto = httpClient.get("search/tv") {
            parameter("query", keyword)
        }.body()
        return response.shows.map { it.toDomain() }
    }

    override suspend fun fetchMediaDetails(id: Int): Show {
        val response: ShowDto = httpClient.get("tv/$id").body()
        return response.toDomain()
    }
}