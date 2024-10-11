package de.ashman.ontrack.api

interface MediaRepository<T> {
    suspend fun fetchMediaByQuery(query: String): Result<List<T>>

    suspend fun fetchMediaDetails(id: String): Result<T>
}
