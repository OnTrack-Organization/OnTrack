package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.entity.user.UserEntity
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface AuthRepository {
    val currentUser: StateFlow<User?>

    suspend fun observeUser(userId: String)
    suspend fun createUser(user: User): Boolean
    suspend fun removeUser()
    suspend fun updateUser(user: User)
    suspend fun isUsernameTaken(username: String): Boolean
    suspend fun signOut()
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
) : AuthRepository {
    private val userCollection = firestore.collection("users")

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        // Firebase Auth initialization might take time, so we need to handle that
        repositoryScope.launch {
            // Wait until Firebase Auth is initialized
            auth.authStateChanged.collect { firebaseAuth ->
                val userId = firebaseAuth?.uid
                if (userId != null) {
                    observeUser(userId) // Now we can observe the user
                }
            }
        }
    }

    override suspend fun observeUser(userId: String) {
        userCollection.document(userId).snapshots.collect { documentSnapshot ->
            try {
                _currentUser.value = documentSnapshot.data<UserEntity>().toDomain()
            } catch (e: Exception) {
                Logger.e { "Error parsing user document: ${e.message}" }
            }
        }
    }

    override suspend fun createUser(user: User): Boolean {
        val document = userCollection.document(user.id).get()
        return if (!document.exists) {
            userCollection.document(user.id).set(user)
            _currentUser.value = user
            Logger.d("User with ID ${user.id} successfully added.")
            true
        } else {
            Logger.w("User with ID ${user.id} already exists.")
            _currentUser.value = user
            false
        }
    }

    override suspend fun updateUser(user: User) {
        userCollection.document(user.id).set(data = user, merge = true)
        _currentUser.value = user
    }

    override suspend fun removeUser() {
        userCollection.document(auth.currentUser?.uid!!).delete()
        _currentUser.value = null
    }

    override suspend fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val querySnapshot = userCollection.where { "username" equalTo username }.get()
            querySnapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            Logger.e { "Error checking username availability: ${e.message}" }
            false
        }
    }
}
