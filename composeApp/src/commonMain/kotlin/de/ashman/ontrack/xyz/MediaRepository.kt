package de.ashman.ontrack.xyz

// TODO shared methods interface
interface MediaRepository<T> {
    suspend fun fetchMediaByQuery(query: String): List<T>

    suspend fun fetchMediaDetails(id: String): T
}
