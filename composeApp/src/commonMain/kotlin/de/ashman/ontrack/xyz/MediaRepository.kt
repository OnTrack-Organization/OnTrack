package de.ashman.ontrack.xyz

interface MediaRepository<T> {
    suspend fun fetchMediaDetails(id: Int): T
}
