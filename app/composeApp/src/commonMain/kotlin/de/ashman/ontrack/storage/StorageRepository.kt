package de.ashman.ontrack.storage

import de.ashman.ontrack.repository.CurrentUserRepository
import dev.gitlive.firebase.storage.FirebaseStorage

interface StorageRepository {
    suspend fun uploadUserImage(bytes: ByteArray): String
}

class StorageRepositoryImpl(
    private val storage: FirebaseStorage,
    private val currentUserRepository: CurrentUserRepository,
): StorageRepository {
    override suspend fun uploadUserImage(bytes: ByteArray): String {
        val ref = storage.reference.child("users/${currentUserRepository.currentUserId}/userImage")

        val data = createDataFromBytes(bytes)
        ref.putData(data)

        return ref.getDownloadUrl()
    }
}