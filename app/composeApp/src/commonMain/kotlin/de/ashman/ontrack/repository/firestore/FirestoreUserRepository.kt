package de.ashman.ontrack.repository.firestore

import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.user.UserEntity
import de.ashman.ontrack.repository.CurrentUserRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FirestoreUserRepository {
    val currentUserId: String

    suspend fun getUser(userId: String): Flow<User?>
    suspend fun createUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun removeUser(): Boolean
    suspend fun isUsernameTaken(username: String): Boolean
    suspend fun doesUserExist(userId: String): Boolean
    suspend fun updateFcmToken(token: String)
    suspend fun signOut()
}

class FirestoreUserRepositoryImpl(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val currentUserRepository: CurrentUserRepository,
) : FirestoreUserRepository {

    private val userCollection = firestore.collection("users")

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override suspend fun createUser(user: User): Boolean {
        return try {
            val document = userCollection.document(user.id).get()

            if (!document.exists) {
                userCollection.document(user.id).set(user.toEntity())
                Logger.d("User created with ID: ${user.id}")
                true
            } else {
                Logger.d("User already exists with ID: ${user.id}")
                false
            }
        } catch (e: Exception) {
            Logger.e { "Error creating user: ${e.message}" }
            false
        }
    }

    override suspend fun getUser(userId: String): Flow<User?> {
        return userCollection.document(userId).snapshots.map { snapshot ->
            try {
                snapshot.data<UserEntity>().toDomain()
            } catch (e: Exception) {
                Logger.e { "Error fetching user $userId: ${e.message}" }
                null
            }
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return try {
            userCollection.document(user.id).set(user.toEntity(), merge = true)
            Logger.d("User updated: ${user.id}")
            true
        } catch (e: Exception) {
            Logger.e { "Error updating user ${user.id}: ${e.message}" }
            false
        }
    }

    override suspend fun removeUser(): Boolean {
        val userId = auth.currentUser?.uid!!

        return try {
            deleteSubcollectionDocuments("friends")
            deleteSubcollectionDocuments("trackings")
            deleteSubcollectionDocuments("recommendations")

            userCollection.document(userId).delete()

            Logger.d("User and all subcollections deleted: $userId")
            true

        } catch (e: Exception) {
            Logger.e { "Error deleting user $userId: ${e.message}" }
            false
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val result = userCollection
                .where { "username" equalTo username }
                .get()

            result.documents.isNotEmpty()
        } catch (e: Exception) {
            Logger.e { "Error checking username $username: ${e.message}" }
            false
        }
    }

    override suspend fun doesUserExist(userId: String): Boolean {
        return try {
            val document = userCollection.document(userId).get()
            document.exists
        } catch (e: Exception) {
            Logger.e { "Error checking existence for user $userId: ${e.message}" }
            false
        }
    }

    override suspend fun updateFcmToken(token: String) {
        val userId = auth.currentUser?.uid!!
        try {
            val docRef = userCollection.document(userId)
            if (docRef.get().exists) {
                docRef.update("fcmToken" to token)
                Logger.d("Updated FCM token for user $userId")
            }
        } catch (e: Exception) {
            Logger.e { "Error updating FCM token for user $userId: ${e.message}" }
        }
    }

    override suspend fun signOut() {
        updateFcmToken("")
        auth.signOut()
        currentUserRepository.clearCurrentUser()
    }

    private suspend fun deleteSubcollectionDocuments(subcollectionName: String) {
        val userId = auth.currentUser?.uid!!
        try {
            val subcollectionRef = userCollection
                .document(userId)
                .collection(subcollectionName)

            val documents = subcollectionRef.get().documents

            for (doc in documents) {
                doc.reference.delete()
            }

            Logger.d("Deleted subcollection $subcollectionName for user $userId")

        } catch (e: Exception) {
            Logger.e { "Error deleting subcollection $subcollectionName for user $userId: ${e.message}" }
        }
    }

}
