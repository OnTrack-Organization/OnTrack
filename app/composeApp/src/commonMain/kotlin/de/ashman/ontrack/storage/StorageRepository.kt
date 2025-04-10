package de.ashman.ontrack.storage

import dev.gitlive.firebase.storage.FirebaseStorage

interface StorageRepository {
    suspend fun uploadUserImage(bytes: ByteArray, fileName: String): String
}

class StorageRepositoryImpl(
    private val storage: FirebaseStorage,
) : StorageRepository {
    override suspend fun uploadUserImage(bytes: ByteArray, fileName: String): String {
        val ref = storage.reference.child("users/${fileName}/userImage")

        val data = createDataFromBytes(bytes)
        ref.putData(data)

        return ref.getDownloadUrl()
    }
}