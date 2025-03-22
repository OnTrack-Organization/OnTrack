package de.ashman.ontrack.network

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.network.dto.UserDto
import de.ashman.ontrack.network.dto.toDomain
import de.ashman.ontrack.network.dto.toDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface UserService {
    suspend fun getUsers(): Result<List<User>>
    suspend fun saveUser(user: User): Result<Unit>
}

class UserServiceImpl(
    private val httpClient: HttpClient
) : UserService {
    override suspend fun getUsers(): Result<List<User>> = safeApiCall {
        val users: List<UserDto> = httpClient.get("users").body()

        users.map { it.toDomain() }
    }

    override suspend fun saveUser(user: User): Result<Unit> = safeApiCall {
        httpClient.post("user") {
            contentType(ContentType.Application.Json)
            setBody(user.toDto())
        }
    }
}
