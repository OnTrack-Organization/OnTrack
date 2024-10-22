package de.ashman.ontrack.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class AccessTokenManager(
    private val httpClient: HttpClient,
    private val clientId: String,
    private val clientSecret: String
) {
    private var accessToken: String? = null
    private var tokenExpiration: Instant? = null

    suspend fun getAccessToken(): String {
        if (isTokenExpired()) {
            fetchAccessToken()
        }
        return accessToken!!
    }

    private suspend fun fetchAccessToken() {
        val response : AccessTokenResponse = httpClient.post {
            parameter("client_id", clientId)
            parameter("client_secret", clientSecret)
            parameter("grant_type", "client_credentials")
        }.body()

        accessToken = response.accessToken
        tokenExpiration = Clock.System.now() + response.expiresIn.seconds

        println("ACCESS TOKEN:" + accessToken)
    }

    private fun isTokenExpired(): Boolean {
        val now = Clock.System.now()
        return tokenExpiration == null || now >= tokenExpiration!!
    }
}
