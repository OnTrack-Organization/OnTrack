package de.ashman.ontrack.media

interface MediaRepository<T> {
    suspend fun fetchMediaByQuery(query: String): Result<List<T>>

    suspend fun fetchMediaDetails(id: String): Result<T>
}
