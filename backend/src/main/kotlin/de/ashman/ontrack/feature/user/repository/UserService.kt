package de.ashman.ontrack.feature.user.repository

import de.ashman.ontrack.feature.user.domain.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getById(id: String): User = userRepository.getReferenceById(id)

    fun findAllById(ids: List<String>): List<User> = userRepository.findAllById(ids)

    fun exists(id: String): Boolean = userRepository.existsById(id)

    fun findByEmail(email: String): User? = userRepository.findOneByEmail(email)

    fun searchByUsername(username: String): List<User> = userRepository.findUsersByUsernameLikeIgnoreCase(username)

    fun save(user: User) {
        userRepository.save(user)
    }
}
