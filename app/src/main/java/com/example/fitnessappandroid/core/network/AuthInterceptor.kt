package com.example.fitnessappandroid.core.network

import com.example.fitnessappandroid.core.data.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the current access token synchronously
        val accessToken = runBlocking {
            tokenManager.getAccessTokenSync()
        }

        val newRequest = if (!accessToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader(
                    NetworkConstants.Headers.AUTHORIZATION,
                    "${NetworkConstants.Headers.BEARER_PREFIX}$accessToken"
                )
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        // Handle token refresh if we get a 401 Unauthorized
        if (response.code == 401) {
            // TODO: Implement token refresh logic
            // For now, we'll just return the response
        }

        return response
    }
}