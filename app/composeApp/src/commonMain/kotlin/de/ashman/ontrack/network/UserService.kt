package de.ashman.ontrack.network

import de.ashman.ontrack.api.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post

interface UserService {
    suspend fun signIn(idToken: String): Result<Unit>
}

class UserServiceImpl(
    private val httpClient: HttpClient
) : UserService {
    override suspend fun signIn(idToken: String): Result<Unit> = safeApiCall {
        httpClient.post("/auth") {
            header("Authorization", "Bearer $idToken")
        }
    }
}
