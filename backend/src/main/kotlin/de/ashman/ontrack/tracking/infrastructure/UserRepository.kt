package de.ashman.ontrack.tracking.infrastructure

import de.ashman.ontrack.tracking.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>
{
    fun existsUserByEmail(email: String): Boolean
}
