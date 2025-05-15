package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.model.User
import de.ashman.ontrack.user.domain.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userRepository: UserJpaRepository
) : UserRepository {
    override fun getById(id: String): User {
        return userRepository.getReferenceById(id)
    }

    override fun exists(id: String): Boolean {
        return userRepository.existsById(id)
    }

    override fun findAllById(ids: List<String>): List<User> {
        return userRepository.findAllById(ids)
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findOneByEmail(email)
    }

    override fun searchByUsername(username: String): List<User> {
        return userRepository.findUsersByUsernameLikeIgnoreCase(username)
    }

    override fun save(user: User) {
        userRepository.save(user)
    }
}
