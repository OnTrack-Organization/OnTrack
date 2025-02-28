package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.user.UserEntity
import de.ashman.ontrack.domain.user.User
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface UserRepository {
    suspend fun getUser(userId: String): User
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun removeCurrentUser()
}

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : UserRepository {
    private val usersCollection = firestore.collection("users")

    override suspend fun getUser(userId: String): User {
        val documentSnapshot = usersCollection.document(userId).get()
        return documentSnapshot.data<UserEntity>().toDomain()
    }

    override suspend fun createUser(user: User) {
        usersCollection.document(user.userData.id).set(user)
    }

    override suspend fun updateUser(user: User) {
        usersCollection
            .document(user.userData.id)
            .set(
                data = user,
                merge = true
            )
    }

    override suspend fun removeCurrentUser() {
        usersCollection
            .document(firebaseAuth.currentUser?.uid!!)
            .delete()
    }
}