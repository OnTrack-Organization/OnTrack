package de.ashman.ontrack.network.services.signin

import de.ashman.ontrack.api.utils.safeBackendApiCall
import de.ashman.ontrack.domain.newdomains.NewUser
import de.ashman.ontrack.network.services.signin.dto.SignInDto
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

interface SignInService {
    suspend fun signIn(fcmToken: String): Result<SignInResult>
    suspend fun signOut(): Result<Unit>
}

class SignInServiceImpl(
    private val httpClient: HttpClient,
    private val firebaseAuth: FirebaseAuth,
) : SignInService {

    override suspend fun signIn(fcmToken: String): Result<SignInResult> = safeBackendApiCall<UserDto> {
        httpClient.post("/sign-in") {
            setBody(SignInDto(fcmToken = fcmToken))
        }
    }.mapCatching { apiResponse ->
        when (apiResponse.status) {
            HttpStatusCode.OK -> SignInResult.ExistingUser(apiResponse.data.toDomain())
            HttpStatusCode.Created -> SignInResult.NewUserCreated(apiResponse.data.toDomain())
            else -> {
                // TODO maybe backend should handle this
                firebaseAuth.currentUser?.delete()
                error("Unexpected status: ${apiResponse.status}")
            }
        }
    }

    override suspend fun signOut(): Result<Unit> = safeBackendApiCall<Unit> {
        httpClient.post("/sign-out")
    }.map {
        firebaseAuth.signOut()
    }
}

sealed class SignInResult {
    data class ExistingUser(val user: NewUser) : SignInResult()
    data class NewUserCreated(val user: NewUser) : SignInResult()
}
