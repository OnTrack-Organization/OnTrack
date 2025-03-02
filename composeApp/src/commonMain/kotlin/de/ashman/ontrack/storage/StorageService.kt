package de.ashman.ontrack.storage

import de.ashman.ontrack.db.AuthRepository
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.vinceglb.filekit.core.PlatformFile

interface StorageService {
    suspend fun uploadUserImage(file: PlatformFile): String
}

class StorageServiceImpl(
    private val storage: FirebaseStorage,
    private val authRepository: AuthRepository,
): StorageService {
    override suspend fun uploadUserImage(file: PlatformFile): String {
        // TODO dont know how right now
        //val ref = storage.reference.child("users/${authService.currentUserId}/$uri")
        return ""
    }
}