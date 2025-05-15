package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface UserJpaRepository : JpaRepository<User, String> {
    fun findOneByEmail(email: String): User?
    fun findUsersByUsernameLikeIgnoreCase(username: String): List<User>
}
