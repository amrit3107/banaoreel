package com.banaoreel.app.data.model

data class VideoJob(
    val id: String,
    val prompt: String,
    val durationSec: Int,
    val status: JobStatus,
    val videoUrl: String?,
    val costPaise: Int,
    val createdAt: String
)

enum class JobStatus {
    QUEUED, SCRIPTING, RENDERING_VISUALS, VOICING, ASSEMBLING, DONE, FAILED
}

data class CreateVideoRequest(
    val prompt: String,
    val durationSec: Int
)
