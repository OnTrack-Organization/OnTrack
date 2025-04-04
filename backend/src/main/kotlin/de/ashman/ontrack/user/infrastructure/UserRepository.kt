package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findOneByEmail(email: String): User?
    fun existsUserByUsername(username: String): Boolean
}
