package de.ashman.ontrack.network.services.friend

import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.newdomains.NewUser
import de.ashman.ontrack.domain.newdomains.OtherUser
import de.ashman.ontrack.domain.newdomains.UserProfile
import de.ashman.ontrack.network.services.friend.dto.OtherUserDto
import de.ashman.ontrack.network.services.friend.dto.UserProfileDto
import de.ashman.ontrack.network.services.friend.dto.toDomain
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post

interface FriendService {
    suspend fun getFriends(): Result<List<NewUser>>
    suspend fun getFriendsAndRequests(): Result<List<OtherUser>>

    suspend fun getUsersByQuery(query: String): Result<List<OtherUser>>

    suspend fun sendRequest(userId: String): Result<Unit>
    suspend fun acceptRequest(userId: String): Result<Unit>
    suspend fun declineRequest(userId: String): Result<Unit>
    suspend fun cancelRequest(userId: String): Result<Unit>

    suspend fun deleteFriend(userId: String): Result<Unit>

    suspend fun getUserProfile(userId: String): Result<UserProfile>
}

class FriendServiceImpl(
    private val httpClient: HttpClient,
) : FriendService {

    override suspend fun getFriends(): Result<List<NewUser>> = safeApiCall {
        httpClient.get("/friends").body<List<UserDto>>().map { it.toDomain() }
    }

    override suspend fun getFriendsAndRequests(): Result<List<OtherUser>> = safeApiCall {
        val response = httpClient.get("/friends-and-requests").body<List<OtherUserDto>>().map { it.toDomain() }
        Logger.d("Friends and requests: $response")
        response
    }

    override suspend fun getUsersByQuery(query: String): Result<List<OtherUser>> = safeApiCall {
        httpClient.get("/user/search?username=$query").body<List<OtherUserDto>>().map { it.toDomain() }
    }

    override suspend fun sendRequest(userId: String): Result<Unit> = safeApiCall {
        httpClient.post("/friend-request/send/$userId")
    }

    override suspend fun acceptRequest(userId: String): Result<Unit> = safeApiCall {
        httpClient.post("/friend-request/accept/$userId")
    }

    override suspend fun declineRequest(userId: String): Result<Unit> = safeApiCall {
        httpClient.post("/friend-request/decline/$userId")
    }

    override suspend fun cancelRequest(userId: String): Result<Unit> = safeApiCall {
        httpClient.post("/friend-request/cancel/$userId")
    }

    override suspend fun deleteFriend(userId: String): Result<Unit> = safeApiCall {
        httpClient.delete("/friend/$userId")
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfile> = safeApiCall {
        httpClient.get("/user/$userId").body<UserProfileDto>().toDomain()
    }
}
