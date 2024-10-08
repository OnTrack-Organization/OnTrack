package de.ashman.ontrack.media

interface MediaRepository<T> {
    suspend fun fetchMediaByQuery(query: String): List<T>

    suspend fun fetchMediaDetails(id: String): T
}
