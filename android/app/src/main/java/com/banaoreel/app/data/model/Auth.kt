package com.banaoreel.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val token: String,
    val userId: String,
    val name: String?,
    val profileComplete: Boolean
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    val id: String,
    val phone: String,
    val name: String?,
    val gender: String?
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val name: String,
    val gender: String?
)
