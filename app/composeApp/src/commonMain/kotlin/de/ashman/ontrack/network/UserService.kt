package de.ashman.ontrack.network

import de.ashman.ontrack.api.utils.safeBackendApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post

interface UserService {
    suspend fun signIn(): Result<Unit>
}

class UserServiceImpl(
    private val httpClient: HttpClient
) : UserService {
    override suspend fun signIn(): Result<Unit> = safeBackendApiCall {
        httpClient.post("/auth") {}
    }
}
