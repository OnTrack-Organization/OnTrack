package de.ashman.ontrack.network.services.account

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.api.utils.safeBackendApiCall
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.network.services.account.dto.AccountSettingsDto
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

interface AccountService {
    suspend fun getCurrentUser(): Result<User>
    suspend fun updateAccountSettings(username: String, name: String): Result<AccountResult>
    suspend fun updateProfilePicture(profilePictureUrl: String): Result<Unit>

    // TODO add remove user method
}

class AccountServiceImpl(
    private val httpClient: HttpClient,
) : AccountService {

    override suspend fun getCurrentUser(): Result<User> = safeApiCall {
        httpClient.get("/account").body<UserDto>().toDomain()
    }

    override suspend fun updateAccountSettings(username: String, name: String): Result<AccountResult> = safeBackendApiCall<String> {
        httpClient.post("/account") {
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
}

sealed class AccountResult {
    object Success : AccountResult()
    data class InvalidUsername(val error: UsernameError?) : AccountResult()
}

