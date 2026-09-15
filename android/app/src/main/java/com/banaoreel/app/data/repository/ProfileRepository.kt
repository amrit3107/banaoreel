package com.banaoreel.app.data.repository

import com.banaoreel.app.data.api.BanaoReelApi
import com.banaoreel.app.data.model.UpdateProfileRequest
import com.banaoreel.app.data.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val api: BanaoReelApi
) {
    suspend fun getMyProfile(): UserProfile = api.getMyProfile()

    suspend fun updateProfile(name: String, gender: String?): UserProfile =
        api.updateProfile(UpdateProfileRequest(name, gender))
}
