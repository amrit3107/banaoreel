package com.banaoreel.app.data.repository

import com.banaoreel.app.data.api.BanaoReelApi
import com.banaoreel.app.data.auth.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: BanaoReelApi,
    private val tokenStore: TokenStore
) {
    suspend fun requestOtp(phone: String) {
        api.requestOtp(mapOf("phone" to phone))
    }

    /** Returns true if the user still needs to complete onboarding (no name on file yet). */
    suspend fun verifyOtp(phone: String, otp: String): Boolean {
        val response = api.verifyOtp(mapOf("phone" to phone, "otp" to otp))
        tokenStore.saveToken(response.token)
        return !response.profileComplete
    }

    suspend fun isLoggedIn(): Boolean = tokenStore.getToken() != null

    suspend fun logout() {
        tokenStore.clear()
    }
}
