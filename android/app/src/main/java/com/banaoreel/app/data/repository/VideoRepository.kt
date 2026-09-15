package com.banaoreel.app.data.repository

import com.banaoreel.app.data.api.BanaoReelApi
import com.banaoreel.app.data.model.CreateVideoRequest
import com.banaoreel.app.data.model.VideoJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val api: BanaoReelApi
) {
    suspend fun createVideo(prompt: String, durationSec: Int): VideoJob =
        api.createVideo(CreateVideoRequest(prompt, durationSec))

    suspend fun getVideo(id: String): VideoJob = api.getVideo(id)

    suspend fun listVideos(): List<VideoJob> = api.listVideos()
}
