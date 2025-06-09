package de.ashman.ontrack.network.services.block

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post

interface BlockService {
    suspend fun blockUser(userId: String): Result<Unit>
    suspend fun unblockUser(userId: String): Result<Unit>
    suspend fun getBlockedUsers(): Result<List<User>>
}

class BlockServiceImpl(
    private val httpClient: HttpClient,
) : BlockService {

    override suspend fun blockUser(userId: String): Result<Unit> = safeApiCall {
        httpClient.post("/block/$userId")
    }

    override suspend fun unblockUser(userId: String): Result<Unit> = safeApiCall {
        httpClient.delete("/block/$userId")
    }

    override suspend fun getBlockedUsers(): Result<List<User>> = safeApiCall {
        httpClient.get("/block/all").body<List<UserDto>>().map { it.toDomain() }
    }
}
