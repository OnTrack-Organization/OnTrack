package de.ashman.ontrack.repository

import de.ashman.ontrack.domain.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CurrentUserRepository {
    val currentUser: StateFlow<User?>
    val currentUserId: String

    fun setCurrentUser(user: User?)
    fun getCurrentUser(): User
    fun clearCurrentUser()
}

class CurrentUserRepositoryImpl : CurrentUserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override val currentUserId: String
        get() = _currentUser.value?.id!!

    override fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    override fun getCurrentUser(): User {
        return _currentUser.value!!
    }

    override fun clearCurrentUser() {
        _currentUser.value = null
    }
}
