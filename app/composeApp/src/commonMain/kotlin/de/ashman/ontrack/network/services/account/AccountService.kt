package de.ashman.ontrack.network.services.account

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.api.utils.safeBackendApiCall
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.network.services.account.dto.AccountSettingsDto
import de.ashman.ontrack.network.services.account.dto.AccountSettingsResponseDto
import de.ashman.ontrack.network.services.account.dto.SignInDto
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

interface AccountService {
    suspend fun signIn(fcmToken: String): Result<AccountResult>
    suspend fun signOut(): Result<Unit>

    suspend fun getCurrentUser(): Result<User>
    suspend fun updateAccountSettings(username: String, name: String): Result<AccountSettingsResult>
    suspend fun updateProfilePicture(profilePictureUrl: String): Result<User>

    suspend fun deleteAccount(): Result<Unit>
}

class AccountServiceImpl(
    private val httpClient: HttpClient,
    private val firebaseAuth: FirebaseAuth,
) : AccountService {

    override suspend fun signIn(fcmToken: String): Result<AccountResult> = safeBackendApiCall<UserDto> {
        httpClient.post("/account/sign-in") {
            setBody(SignInDto(fcmToken = fcmToken))
        }
    }.mapCatching { apiResponse ->
        when (apiResponse.status) {
            HttpStatusCode.OK -> AccountResult.ExistingUser(apiResponse.data.toDomain())
            HttpStatusCode.Created -> AccountResult.NewUserCreated(apiResponse.data.toDomain())
            else -> {
                firebaseAuth.currentUser?.delete()
                error("Unexpected status: ${apiResponse.status}")
            }
        }
    }

    override suspend fun signOut(): Result<Unit> = safeBackendApiCall<Unit> {
        httpClient.post("/account/sign-out")
    }.map {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): Result<User> = safeApiCall {
        httpClient.get("/account").body<UserDto>().toDomain()
    }

    override suspend fun updateAccountSettings(username: String, name: String): Result<AccountSettingsResult> = safeBackendApiCall<AccountSettingsResponseDto> {
        httpClient.post("/account/settings") {
            setBody(AccountSettingsDto(username = username, name = name))
        }
    }.mapCatching { apiResponse ->
        when (apiResponse.status) {
            HttpStatusCode.OK -> {
                val userDto = apiResponse.data.user ?: error("User data missing")
                AccountSettingsResult.Success(userDto.toDomain())
            }

            HttpStatusCode.Conflict -> {
                val errorMsg = apiResponse.data.error
                val error = mapUsernameError(errorMsg)
                AccountSettingsResult.InvalidUsername(error)
            }

            else -> error("Unexpected status: ${apiResponse.status}")
        }
    }

    override suspend fun updateProfilePicture(profilePictureUrl: String): Result<User> = safeBackendApiCall<UserDto> {
        httpClient.post("/account/profile-picture") {
            setBody(profilePictureUrl)
        }
    }.mapCatching { it.data.toDomain() }

    override suspend fun deleteAccount(): Result<Unit> = safeApiCall<Unit> {
        httpClient.delete("/account")
    }
}

sealed class AccountResult {
    data class ExistingUser(val user: User) : AccountResult()
    data class NewUserCreated(val user: User) : AccountResult()
}

sealed class AccountSettingsResult {
    data class Success(val user: User) : AccountSettingsResult()
    data class InvalidUsername(val error: UsernameError?) : AccountSettingsResult()
}
