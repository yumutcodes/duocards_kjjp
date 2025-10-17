package com.example.fitnessappandroid.core.network

import com.example.fitnessappandroid.core.data.TokenManager
import com.example.fitnessappandroid.core.di.UnauthenticatedClient
import com.example.fitnessappandroid.features.auth.data.AuthApiService
import com.example.fitnessappandroid.features.auth.data.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TokenAuthenticator handles 401 Unauthorized responses by attempting to refresh the access token.
 * This is the proper OkHttp mechanism for handling authentication challenges.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    @UnauthenticatedClient private val authApiService: AuthApiService
) : Authenticator {

    // Mutex to prevent multiple concurrent refresh attempts
    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't try to refresh for auth endpoints themselves
        if (isAuthEndpoint(response.request.url.encodedPath)) {
            return null
        }

        // If we already tried to authenticate this request, give up
        if (responseCount(response) >= 3) {
            return null // Too many failed attempts
        }

        return runBlocking {
            refreshMutex.withLock {
                // Get the current token after acquiring the lock
                // Another thread might have already refreshed it
                val currentToken = tokenManager.getAccessTokenSync()
                val requestToken = response.request.header(NetworkConstants.Headers.AUTHORIZATION)
                    ?.removePrefix(NetworkConstants.Headers.BEARER_PREFIX)

                // If the token has changed since the request was made, retry with the new token
                if (currentToken != null && currentToken != requestToken) {
                    return@runBlocking response.request.newBuilder()
                        .header(
                            NetworkConstants.Headers.AUTHORIZATION,
                            "${NetworkConstants.Headers.BEARER_PREFIX}$currentToken"
                        )
                        .build()
                }

                // Attempt to refresh the token
                val refreshSuccess = refreshToken()

                if (refreshSuccess) {
                    val newAccessToken = tokenManager.getAccessTokenSync()
                    if (newAccessToken != null) {
                        // Return the request with the new token
                        return@runBlocking response.request.newBuilder()
                            .header(
                                NetworkConstants.Headers.AUTHORIZATION,
                                "${NetworkConstants.Headers.BEARER_PREFIX}$newAccessToken"
                            )
                            .build()
                    }
                }

                // Refresh failed, clear tokens
                tokenManager.clearTokens()
                null // Return null to indicate authentication failed
            }
        }
    }

    /**
     * Attempts to refresh the access token using the refresh token
     * @return true if successful, false otherwise
     */
    private suspend fun refreshToken(): Boolean {
        return try {
            val refreshToken = tokenManager.getRefreshTokenSync()
            if (refreshToken.isNullOrEmpty()) {
                return false
            }

            // Call the refresh endpoint
            val response = authApiService.refreshToken(
                RefreshTokenRequest(refreshToken)
            )

            if (response.isSuccessful) {
                response.body()?.let { refreshResponse ->
                    // Save the new tokens
                    tokenManager.saveTokens(
                        accessToken = refreshResponse.token,
                        refreshToken = refreshResponse.refreshToken
                    )
                    return true
                }
            }

            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Counts how many times the request has been authenticated
     */
    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse = response.priorResponse
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }

    /**
     * Checks if the endpoint is an auth endpoint that shouldn't trigger refresh
     */
    private fun isAuthEndpoint(path: String): Boolean {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/auth/refresh")
    }
}

