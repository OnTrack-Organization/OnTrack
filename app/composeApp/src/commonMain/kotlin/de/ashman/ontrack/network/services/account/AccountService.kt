package de.ashman.ontrack.network.services.account

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.api.utils.safeBackendApiCall
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.network.services.account.dto.AccountSettingsDto
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
    suspend fun updateAccountSettings(username: String, name: String): Result<AccountResult>
    suspend fun updateProfilePicture(profilePictureUrl: String): Result<Unit>

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
                // TODO maybe backend should handle this
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

    override suspend fun updateAccountSettings(username: String, name: String): Result<AccountResult> = safeBackendApiCall<String> {
        httpClient.post("/account/settings") {
            setBody(AccountSettingsDto(username = username, name = name))
        }
    }.mapCatching { apiResponse ->
        when (apiResponse.status) {
            HttpStatusCode.OK -> AccountResult.Success
            HttpStatusCode.Conflict -> {
                val error = mapUsernameError(apiResponse.data)
                AccountResult.InvalidUsername(error)
            }

            else -> error("Unexpected status: ${apiResponse.status}")
        }
    }

    override suspend fun updateProfilePicture(profilePictureUrl: String): Result<Unit> = safeBackendApiCall<Unit> {
        httpClient.post("/account/profile-picture") {
            setBody(profilePictureUrl)
        }
    }.mapCatching { apiResponse ->
        when (apiResponse.status) {
            HttpStatusCode.OK -> Unit
            else -> error("Unexpected status: ${apiResponse.status}")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = safeApiCall<Unit> {
        httpClient.delete("/account")
    }
}

sealed class AccountResult {
    data class ExistingUser(val user: User) : AccountResult()
    data class NewUserCreated(val user: User) : AccountResult()
    object Success : AccountResult()
    data class InvalidUsername(val error: UsernameError?) : AccountResult()
}

