package de.ashman.ontrack.network.services.friend

import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.user.OtherUser
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.domain.user.UserProfile
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import de.ashman.ontrack.network.services.friend.dto.OtherUserDto
import de.ashman.ontrack.network.services.friend.dto.UserProfileDto
import de.ashman.ontrack.network.services.friend.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post

interface FriendService {
    suspend fun getFriends(): Result<List<User>>
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

    override suspend fun getFriends(): Result<List<User>> = safeApiCall {
        httpClient.get("/friend/all").body<List<UserDto>>().map { it.toDomain() }
    }

    override suspend fun getFriendsAndRequests(): Result<List<OtherUser>> = safeApiCall {
        val response = httpClient.get("/friend/friends-and-requests").body<List<OtherUserDto>>().map { it.toDomain() }
        Logger.d("Friends and requests: $response")
        response
    }

    override suspend fun deleteFriend(userId: String): Result<Unit> = safeApiCall {
        httpClient.delete("/friend/$userId")
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

    override suspend fun getUserProfile(userId: String): Result<UserProfile> = safeApiCall {
        httpClient.get("/user/$userId").body<UserProfileDto>().toDomain()
    }
}
