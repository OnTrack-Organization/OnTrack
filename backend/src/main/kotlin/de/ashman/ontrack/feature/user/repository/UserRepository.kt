package de.ashman.ontrack.feature.user.repository

import de.ashman.ontrack.feature.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findOneByEmail(email: String): User?

    // Find 10 users where name matches and ignore current user
    fun findTop10ByUsernameContainingIgnoreCaseAndIdNotOrderByUsernameAsc(
        username: String,
        excludedId: String
    ): List<User>

    fun findByUsernameIn(usernames: Set<String>): Set<User>
}
