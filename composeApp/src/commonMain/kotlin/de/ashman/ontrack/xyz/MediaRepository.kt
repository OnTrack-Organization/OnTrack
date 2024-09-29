package de.ashman.ontrack.xyz

// TODO shared methods interface
interface MediaRepository<T> {
    suspend fun fetchMediaByKeyword(keyword: String): List<T>

    suspend fun fetchMediaDetails(id: Int): T
}
