package de.ashman.ontrack.feature.user.repository

import de.ashman.ontrack.feature.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findOneByEmail(email: String): User?
    fun findUsersByUsernameLikeIgnoreCase(username: String): List<User>
}
