package de.ashman.ontrack.user.domain.repository

import de.ashman.ontrack.user.domain.model.User

interface UserRepository {
    fun getById(id: String): User
    fun exists(id: String): Boolean
    fun findAllById(ids: List<String>): List<User>
    fun findByEmail(email: String): User?
    fun searchByUsername(username: String): List<User>
    fun save(user: User)
}
